package com.vertcdemo.solution.interactivelive.core

import android.view.SurfaceView
import android.view.TextureView
import androidx.lifecycle.ViewModel
import com.vertcdemo.solution.interactivelive.core.events.ForwardInfo
import com.vertcdemo.solution.interactivelive.ui.room.RoomArgs
import com.vertcdemo.solution.interactivelive.utils.NetworkState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface IRTCEngine {
    val networkState: StateFlow<NetworkState>

    fun startVideoCapture() {}
    fun startAudioCapture() {}
    fun stopVideoCapture() {}
    fun stopAudioCapture() {}

    fun stopCapture() {
        stopVideoCapture()
        stopAudioCapture()
    }

    fun switchCamera() {}
    fun setLocalVideoView(view: SurfaceView?) {}
    fun setLocalVideoView(view: TextureView?) {}

    fun setRemoteVideoView(userId: String, view: SurfaceView?) {}
    fun setRemoteVideoView(userId: String, view: TextureView?) {}

    fun joinChat(args: RoomArgs) {}

    fun joinRTCRoom(roomId: String, userId: String, token: String) {}

    fun startLive(args: RoomArgs) {}

    fun stopLive() {}

    fun startAnchorLink(args: RoomArgs, forwardInfo: ForwardInfo, anchorUserId: String) {}

    fun stopAnchorLink(args: RoomArgs) {}

    fun startAudienceLink(args: RoomArgs, audienceUserIds: List<String>) {}

    fun stopAudienceLink(args: RoomArgs) {}
}

class EmptyEngine() : ViewModel(), IRTCEngine {
    override val networkState: StateFlow<NetworkState> = MutableStateFlow(NetworkState.NONE)
}