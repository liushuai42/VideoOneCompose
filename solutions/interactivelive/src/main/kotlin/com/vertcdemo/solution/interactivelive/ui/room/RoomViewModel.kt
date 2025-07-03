package com.vertcdemo.solution.interactivelive.ui.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vertcdemo.solution.interactivelive.core.IRTCEngine
import com.vertcdemo.solution.interactivelive.core.annotation.LiveFinishType
import com.vertcdemo.solution.interactivelive.core.events.LiveFinishEvent
import com.vertcdemo.solution.interactivelive.network.LiveService
import com.vertcdemo.solution.interactivelive.ui.room.host.LiveFinishReason
import com.vertcdemo.solution.interactivelive.ui.toast
import com.vertcdemo.solution.interactivelive.utils.BString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe

abstract class RoomViewModel(val args: RoomArgs, val engine: IRTCEngine) : ViewModel() {
    private val _liveFinish = Channel<LiveFinishReason>()
    val liveFinish = _liveFinish.receiveAsFlow()

    fun switchCamera() {
        engine.switchCamera()
    }

    fun requestFinishLive() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                LiveService.finishLive(args.rtsRoomId)
                // Let onLiveFinishEvent to handle the next action
            }.onFailure {
                _liveFinish.send(LiveFinishReason.Failed)
            }
        }
    }

    // region EventBus
    @Subscribe
    fun onLiveFinishEvent(event: LiveFinishEvent) {
        viewModelScope.launch(Dispatchers.Main) {
            when (event.type) {
                LiveFinishType.TIMEOUT -> {
                    toast(BString.minutes_error_message)
                    _liveFinish.send(LiveFinishReason.Timeout)
                }

                LiveFinishType.IRREGULARITY -> {
                    toast(BString.closed_terms_service)
                    _liveFinish.send(LiveFinishReason.Irregularity)
                }

                LiveFinishType.NORMAL -> {
                    _liveFinish.send(LiveFinishReason.End(event.liveSummary))
                }
            }
        }
    }
    // endregion
}