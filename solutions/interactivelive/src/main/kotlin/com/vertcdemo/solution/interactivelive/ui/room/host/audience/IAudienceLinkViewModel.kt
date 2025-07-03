package com.vertcdemo.solution.interactivelive.ui.room.host.audience

import android.os.SystemClock
import com.vertcdemo.solution.interactivelive.core.IRTCEngine
import com.vertcdemo.solution.interactivelive.core.annotation.LivePermitType
import com.vertcdemo.solution.interactivelive.core.annotation.RoomStatus
import com.vertcdemo.solution.interactivelive.core.events.AudienceLinkApplyEvent
import com.vertcdemo.solution.interactivelive.core.events.AudienceLinkCancelEvent
import com.vertcdemo.solution.interactivelive.core.events.AudienceLinkFinishEvent
import com.vertcdemo.solution.interactivelive.core.events.AudienceLinkStatusEvent
import com.vertcdemo.solution.interactivelive.core.events.RoomStatusEvent
import com.vertcdemo.solution.interactivelive.core.events.UserMediaChangedEvent
import com.vertcdemo.solution.interactivelive.data.AudienceLinkRequest
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo
import com.vertcdemo.solution.interactivelive.ui.room.RoomArgs
import com.vertcdemo.solution.interactivelive.ui.room.host.DialogState
import com.vertcdemo.solution.interactivelive.ui.room.host.IDialogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.greenrobot.eventbus.Subscribe

const val TAB_AUDIENCE_LINK_LINKED = 0
const val TAB_AUDIENCE_LINK_APPLICATIONS = 1

interface IAudienceLinkViewModel {
    val manageAudienceDialog: IDialogState

    val manageAudiencesTabIndex: StateFlow<Int>

    fun setManageAudiencesTab(index: Int)

    fun permitAudienceLink(request: AudienceLinkRequest, @LivePermitType permitType: Int) {}

    val audienceLinkUsers: StateFlow<List<LiveUserInfo>>

    val audienceLinkApplications: StateFlow<List<AudienceLinkRequest>>

    val newApplicationApplyDot: StateFlow<Boolean>

    val audienceLinkStartTime: Long
}

class AudienceLinkViewModel(
    private val args: RoomArgs,
    private val engine: IRTCEngine
) :
    IAudienceLinkViewModel {
    override val manageAudienceDialog = DialogState()

    private val _manageAudiencesTabIndex = MutableStateFlow(TAB_AUDIENCE_LINK_LINKED)
    override val manageAudiencesTabIndex = _manageAudiencesTabIndex.asStateFlow()

    override fun setManageAudiencesTab(index: Int) {
        _manageAudiencesTabIndex.value = index
        if (index == TAB_AUDIENCE_LINK_APPLICATIONS) {
            _newApplicationApplyDot.value = false
        }
    }

    private val _audienceLinkUsers = MutableStateFlow<List<LiveUserInfo>>(emptyList())
    override val audienceLinkUsers = _audienceLinkUsers.asStateFlow()

    private val _audienceLinkApplications = MutableStateFlow<List<AudienceLinkRequest>>(emptyList())
    override val audienceLinkApplications = _audienceLinkApplications.asStateFlow()

    private val _newApplicationApplyDot = MutableStateFlow(false)
    override val newApplicationApplyDot = _newApplicationApplyDot.asStateFlow()

    override var audienceLinkStartTime: Long = -1
        private set

    // EventBus
    @Subscribe
    fun onAudienceLinkApply(event: AudienceLinkApplyEvent) {
        val oldList = _audienceLinkApplications.value
        if (oldList.isEmpty()) {
            _audienceLinkApplications.value = listOf(event)
        } else {
            val filtered = oldList.filter { it.userId != event.userId }
            _audienceLinkApplications.value = filtered + event
        }

        if (_manageAudiencesTabIndex.value != TAB_AUDIENCE_LINK_APPLICATIONS) {
            _newApplicationApplyDot.value = true
        }
    }

    @Subscribe
    fun onAudienceLinkCancel(event: AudienceLinkCancelEvent) {
        removeAudienceLinkRequestByUerId(event.userId)
        if (_audienceLinkApplications.value.isEmpty()) {
            _newApplicationApplyDot.value = false
        }
    }

    @Subscribe
    fun onAudienceLinkJoin(event: AudienceLinkStatusEvent.Join) {
        removeAudienceLinkRequestByUerId(event.userId)
        val audiences = event.userList.filter { it.userId != args.hostUserId }
        _audienceLinkUsers.value = audiences

        if (audiences.size == 1) {
            audienceLinkStartTime = SystemClock.uptimeMillis()
        }

        engine.startAudienceLink(args = args, audiences.map { it.userId })
    }

    @Subscribe
    fun onAudienceLinkLeave(event: AudienceLinkStatusEvent.Leave) {
        val audiences = event.userList.filter { it.userId != args.hostUserId }
        _audienceLinkUsers.value = audiences

        engine.startAudienceLink(args = args, audiences.map { it.userId })
    }

    @Subscribe
    fun onAudienceLinkFinishEvent(event: AudienceLinkFinishEvent) {
        _audienceLinkUsers.value = emptyList()
        audienceLinkStartTime = -1L

        engine.stopAudienceLink(args = args)
    }

    @Subscribe
    fun onRoomStatusEvent(event: RoomStatusEvent) {
        if (event.status != RoomStatus.AUDIENCE_LINK) {
            _audienceLinkUsers.value = emptyList()
        }
    }

    @Subscribe
    fun onUserMediaChangedEvent(event: UserMediaChangedEvent) {
        val users = _audienceLinkUsers.value
        if (users.isEmpty()) {
            return
        }

        _audienceLinkUsers.value = users.map {
            if (it.userId == event.userId) {
                it.copyFrom(event)
            } else {
                it
            }
        }
    }
    // endregion

    fun removeAudienceLinkRequestByUerId(userId: String) {
        _audienceLinkApplications.value =
            _audienceLinkApplications.value.filter { it.userId != userId }
    }
}
