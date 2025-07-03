package com.vertcdemo.solution.interactivelive.ui.room.host.anchor

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vertcdemo.base.network.HttpException
import com.vertcdemo.base.utils.ApplicationProvider
import com.vertcdemo.base.utils.ErrorTool
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.bus.LiveEventBus
import com.vertcdemo.solution.interactivelive.core.annotation.LivePermitType
import com.vertcdemo.solution.interactivelive.core.events.AnchorLinkAcceptedEvent
import com.vertcdemo.solution.interactivelive.core.events.AnchorLinkFinishEvent
import com.vertcdemo.solution.interactivelive.core.events.AnchorLinkInviteEvent
import com.vertcdemo.solution.interactivelive.core.events.ForwardInfo
import com.vertcdemo.solution.interactivelive.network.LiveService
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo
import com.vertcdemo.solution.interactivelive.ui.room.RoomArgs
import com.vertcdemo.solution.interactivelive.ui.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnchorLinkViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun requestAnchorList() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                LiveService.getAnchorList()
            }.onSuccess {
                _uiState.value = UiState.Loaded(it.anchorList)
            }.onFailure {
                _uiState.value = UiState.Loaded()
            }
        }
    }

    fun inviteAnchor(roomArgs: RoomArgs, anchor: LiveUserInfo) {
        viewModelScope.launch {
            runCatching {
                LiveService.inviteAnchorLink(
                    inviterRoomId = roomArgs.rtsRoomId,
                    inviterUserId = roomArgs.hostUserId,
                    inviteeRoomId = anchor.roomId,
                    inviteeUserId = anchor.userId,
                    extra = ""
                )
            }.onSuccess {
                Toast.makeText(
                    ApplicationProvider.get(),
                    R.string.anchor_pk_invitation_sent,
                    Toast.LENGTH_SHORT
                ).show()
            }.onFailure {
                if (it is HttpException) {
                    if (it.code == 622) {
                        Toast.makeText(
                            ApplicationProvider.get(),
                            R.string.anchor_pk_invitation_sent,
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            ApplicationProvider.get(),
                            ErrorTool.getErrorMessage(it),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun replyAnchorLinkInvite(
        args: RoomArgs,
        event: AnchorLinkInviteEvent,
        @LivePermitType replyType: Int
    ) {
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            val response = LiveService.replyAnchorLinkInvite(
                linkerId = event.linkerId,
                inviterRoomId = event.userInfo.roomId,
                inviterUserId = event.userInfo.userId,
                inviteeRoomId = args.rtsRoomId,
                inviteeUserId = args.hostUserId,
                replyType = replyType
            )

            if (replyType == LivePermitType.ACCEPT) {
                // We need using a local event to notify the link data (linkerId & users)
                LiveEventBus.post(
                    AnchorLinkAcceptedEvent(
                        event.linkerId,
                        ForwardInfo(response.rtcRoomId!!, response.rtcToken!!),
                        requireNotNull(response.userList) { "userList is null" }
                    )
                )
            }
        }
    }

    fun finishAnchorLink(
        linkerId: String,
        roomId: String,
    ) {
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            try {
                LiveService.finishAnchorLink(
                    linkerId = linkerId,
                    roomId = roomId,
                )
            } catch (e: HttpException) {
                if (e.code == 560) {
                    // record not found
                    // Status error, so we fake an AnchorLinkFinishEvent
                    LiveEventBus.post(AnchorLinkFinishEvent(roomId))
                } else {
                    launch(Dispatchers.Main) {
                        toast(ErrorTool.getErrorMessage(e))
                    }
                }
            }
        }
    }
}
