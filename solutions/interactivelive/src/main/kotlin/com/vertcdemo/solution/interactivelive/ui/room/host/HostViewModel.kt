package com.vertcdemo.solution.interactivelive.ui.room.host

import android.os.SystemClock
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vertcdemo.base.network.HttpException
import com.vertcdemo.base.utils.ErrorTool
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.bus.LiveEventBus
import com.vertcdemo.solution.interactivelive.core.IRTCEngine
import com.vertcdemo.solution.interactivelive.core.RTCEngineViewModel
import com.vertcdemo.solution.interactivelive.core.annotation.InviteReply
import com.vertcdemo.solution.interactivelive.core.annotation.LivePermitType
import com.vertcdemo.solution.interactivelive.core.annotation.MediaStatus
import com.vertcdemo.solution.interactivelive.core.annotation.MessageType
import com.vertcdemo.solution.interactivelive.core.annotation.RoomStatus
import com.vertcdemo.solution.interactivelive.core.events.AnchorLinkAcceptedEvent
import com.vertcdemo.solution.interactivelive.core.events.AnchorLinkFinishEvent
import com.vertcdemo.solution.interactivelive.core.events.AnchorLinkInviteEvent
import com.vertcdemo.solution.interactivelive.core.events.AnchorLinkReplyEvent
import com.vertcdemo.solution.interactivelive.core.events.AudienceLinkKickResultEvent
import com.vertcdemo.solution.interactivelive.core.events.ForwardInfo
import com.vertcdemo.solution.interactivelive.core.events.LiveRTSUserEvent
import com.vertcdemo.solution.interactivelive.core.events.MessageEvent
import com.vertcdemo.solution.interactivelive.core.events.RoomStatusEvent
import com.vertcdemo.solution.interactivelive.data.AudienceLinkRequest
import com.vertcdemo.solution.interactivelive.network.LiveService
import com.vertcdemo.solution.interactivelive.network.data.GiftEvent
import com.vertcdemo.solution.interactivelive.network.data.IGiftEvent
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo
import com.vertcdemo.solution.interactivelive.ui.room.RoomArgs
import com.vertcdemo.solution.interactivelive.ui.room.RoomViewModel
import com.vertcdemo.solution.interactivelive.ui.room.host.audience.AudienceLinkViewModel
import com.vertcdemo.solution.interactivelive.ui.room.host.audience.IAudienceLinkViewModel
import com.vertcdemo.solution.interactivelive.ui.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe

private const val TAG = "HostViewModel"

class HostViewModel(
    args: RoomArgs,
    engine: IRTCEngine,
    private val audienceLinkViewModel: AudienceLinkViewModel = AudienceLinkViewModel(args, engine),
    private val chatViewModel: ChatViewModel = ChatViewModel()
) : RoomViewModel(args, engine),
    IAudienceLinkViewModel by audienceLinkViewModel,
    IChatViewModel by chatViewModel {
    private val _hostUserInfo = MutableStateFlow(args.userInfo)
    val hostUserInfo = _hostUserInfo.asStateFlow()

    private val _audienceCount = MutableStateFlow(0)
    val audienceCount = _audienceCount.asStateFlow()

    private val _roomStatus = MutableStateFlow(RoomStatus.LIVE)
    val roomStatus = _roomStatus.asStateFlow()

    var anchorLinkId: String? = null
        private set

    var anchorLinkStartTime: Long = -1L
        private set

    private val _anchorLinkedUsers = MutableStateFlow<List<LiveUserInfo>>(emptyList())
    val anchorLinkedUsers = _anchorLinkedUsers.asStateFlow()

    fun toggleMicrophone() {
        val user = _hostUserInfo.value
        _hostUserInfo.value = if (user.isMicOn) {
            engine.stopAudioCapture()

            user.copy(mic = MediaStatus.OFF)
        } else {
            engine.startAudioCapture()

            user.copy(mic = MediaStatus.ON)
        }
    }

    fun toggleCamera() {
        val user = _hostUserInfo.value
        _hostUserInfo.value = if (user.isCameraOn) {
            engine.stopVideoCapture()

            user.copy(camera = MediaStatus.OFF)
        } else {
            engine.startVideoCapture()

            user.copy(camera = MediaStatus.ON)
        }
    }

    val confirmFinishDialog: IDialogState = DialogState()
    val anchorLinkInviteDialog: IDialogState = DialogState()
    val anchorLinkFinishDialog: IDialogState = DialogState()
    val anchorLinkReceivedDialog: ArgumentDialogState = ArgumentDialogState()
    val moreActionsDialog: IDialogState = DialogState()

    /**
     * 直播开始时间
     */
    val startTime: Long = SystemClock.uptimeMillis()

    fun startLive() {
        engine.joinChat(args)
        engine.startLive(args)
    }

    init {
        LiveEventBus.register(this)
        LiveEventBus.register(audienceLinkViewModel)
    }

    override fun onCleared() {
        LiveEventBus.unregister(this)
        LiveEventBus.unregister(audienceLinkViewModel)
        super.onCleared()
    }

    // EventBus
    @Subscribe
    fun onLiveRTSUserEvent(event: LiveRTSUserEvent) {
        _audienceCount.value = event.audienceCount
    }

    @Subscribe
    fun onRoomStatusEvent(event: RoomStatusEvent) {
        _roomStatus.value = event.status
    }

    private val _gifts = Channel<IGiftEvent>(capacity = Channel.Factory.BUFFERED)
    val gifts = _gifts.receiveAsFlow()

    @Subscribe
    fun onMessageEvent(event: MessageEvent) {
        val body = event.body
        if (body.type == MessageType.MSG) {
            body.content?.let {
                val isHost = event.user.userId == args.hostUserId
                chatViewModel.onMessage(event.user.userName, it, isHost)
            }
        } else if (body.type == MessageType.GIFT) {
            viewModelScope.launch {
                _gifts.send(
                    GiftEvent(
                        giftType = body.giftType,
                        userId = event.user.userId,
                        username = event.user.userName,
                    )
                )
            }
        }
    }

    // region Anchor Link
    @Subscribe
    fun onAnchorLinkInviteEvent(event: AnchorLinkInviteEvent) {
        anchorLinkReceivedDialog.show(event)
    }

    @Subscribe
    fun onAnchorLinkReplyEvent(event: AnchorLinkReplyEvent) {
        if (event.replyType == InviteReply.ACCEPT) {
            startAnchorLink(
                event.linkerId,
                ForwardInfo(event.rtcRoomId, event.rtcToken),
                event.rtcUserList!!
            )
        } else if (event.replyType == InviteReply.REJECT) {
            viewModelScope.launch {
                toast(R.string.pk_invitation_reject)
            }
        }
    }

    @Subscribe
    fun onAnchorLinkAcceptedEvent(event: AnchorLinkAcceptedEvent) {
        startAnchorLink(event.linkerId, event.forwardInfo, event.rtcUserList)
    }

    private fun startAnchorLink(
        linkId: String,
        forwardInfo: ForwardInfo,
        rtcUserList: List<LiveUserInfo>
    ) {
        anchorLinkId = linkId
        if (anchorLinkStartTime == -1L) {
            anchorLinkStartTime = SystemClock.uptimeMillis()
        }
        val anchors = rtcUserList.filter {
            it.userId != args.hostUserId
        }
        _anchorLinkedUsers.value = anchors

        val anchorUserId = anchors.first().userId

        engine.startAnchorLink(
            args,
            forwardInfo,
            anchorUserId,
        )
    }

    @Subscribe
    fun onAnchorLinkFinishEvent(event: AnchorLinkFinishEvent) {
        anchorLinkFinishDialog.dismiss()
        anchorLinkId = null
        anchorLinkStartTime = -1L
        _anchorLinkedUsers.value = emptyList()
        engine.stopAnchorLink(args)
    }
    // endregion

    // endregion

    override fun permitAudienceLink(request: AudienceLinkRequest, @LivePermitType permitType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (permitType == LivePermitType.ACCEPT) {
                    // MUST JOIN RTC room first, or we will miss room's message
                    engine.joinRTCRoom(
                        args.rtcRoomId,
                        args.hostUserId,
                        args.rtcToken,
                    )
                }
                LiveService.permitAudienceLink(
                    request.linkerId,
                    args.rtsRoomId,
                    args.hostUserId,
                    args.rtsRoomId,
                    request.userId,
                    permitType,
                )
                if (permitType == LivePermitType.ACCEPT) {
                    // Update room status to audience link
                    // check if first audience link
                    // set startAudienceLinkTime = SystemClock.uptimeMillis()
                    launch(Dispatchers.Main) {
                        toast(R.string.first_audience_link_message)
                    }
                }
            } catch (e: HttpException) {
                launch(Dispatchers.Main) {
                    toast(ErrorTool.getErrorMessage(e))
                }
            }

            // When rejected, No subsequence Event, so remove the request manually.
            audienceLinkViewModel.removeAudienceLinkRequestByUerId(request.applicant.userId)
        }
    }

    fun finishAudienceLink() {
        viewModelScope.launch(Dispatchers.IO) {
            LiveService.finishAudienceLink(args.rtsRoomId)
            launch(Dispatchers.Main) {
                toast(R.string.audience_link_disconnect_all)
            }
        }
    }

    fun finishAudienceLink(userInfo: LiveUserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            LiveService.kickAudienceLink(
                args.rtsRoomId,
                args.hostUserId,
                args.rtsRoomId,
                userInfo.userId,
            )
            LiveEventBus.post(AudienceLinkKickResultEvent(userInfo.userId))
        }
    }

    fun manageAudienceMedia(
        userInfo: LiveUserInfo,
        camera: Int = MediaStatus.KEEP,
        mic: Int = MediaStatus.KEEP
    ) {
        if (camera == MediaStatus.KEEP && mic == MediaStatus.KEEP) {
            return
        }

        if (camera == MediaStatus.ON || mic == MediaStatus.ON) {
            throw IllegalArgumentException("Only support close camera and mic")
        }

        viewModelScope.launch(Dispatchers.IO) {
            LiveService.manageGuestMedia(
                hostRoomId = args.rtsRoomId,
                hostUserId = args.hostUserId,
                guestRoomId = args.rtsRoomId,
                guestUserId = userInfo.userId,
                camera = camera,
                mic = mic,
            )
        }
    }


    companion object {
        val ROOM_ARGS_KEY = CreationExtras.Companion.Key<RoomArgs>()

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val args = this[ROOM_ARGS_KEY]!!
                val engine = this[RTCEngineViewModel.Companion.ENGINE_KEY]!!
                HostViewModel(args, engine)
            }
        }

        /**
         * 最多 6 个连麦观众
         */
        const val MAX_LINK_COUNT = 6
    }
}