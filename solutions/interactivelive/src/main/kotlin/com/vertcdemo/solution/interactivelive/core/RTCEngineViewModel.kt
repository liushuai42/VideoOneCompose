package com.vertcdemo.solution.interactivelive.core

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.SurfaceView
import android.view.TextureView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ss.bytertc.engine.RTCRoom
import com.ss.bytertc.engine.RTCRoomConfig
import com.ss.bytertc.engine.RTCVideo
import com.ss.bytertc.engine.UserInfo
import com.ss.bytertc.engine.VideoCanvas
import com.ss.bytertc.engine.VideoEncoderConfig
import com.ss.bytertc.engine.data.CameraId
import com.ss.bytertc.engine.data.EngineConfig
import com.ss.bytertc.engine.data.ForwardStreamInfo
import com.ss.bytertc.engine.data.RemoteStreamKey
import com.ss.bytertc.engine.data.StreamIndex
import com.ss.bytertc.engine.handler.IRTCVideoEventHandler
import com.ss.bytertc.engine.type.ChannelProfile
import com.ss.bytertc.engine.type.NetworkQuality
import com.ss.bytertc.engine.type.NetworkQualityStats
import com.ss.bytertc.engine.video.VideoCaptureConfig
import com.vertcdemo.solution.interactivelive.core.events.ForwardInfo
import com.vertcdemo.solution.interactivelive.rtc.RTCRoomEventHandlerRTS
import com.vertcdemo.solution.interactivelive.rtc.RTCVideoEventHandlerRTS
import com.vertcdemo.solution.interactivelive.rtc.RTCVideoStream
import com.vertcdemo.solution.interactivelive.rtc.RTCVideoTranscoding
import com.vertcdemo.solution.interactivelive.rtc.VideoTranscoding
import com.vertcdemo.solution.interactivelive.ui.room.RoomArgs
import com.vertcdemo.solution.interactivelive.utils.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RTCEngineViewModel(
    application: Application,
    appId: String,
    bid: String,
) : ViewModel(), IRTCEngine {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val singleDispatcher = Dispatchers.Default.limitedParallelism(1)

    private val videoEventHandler: IRTCVideoEventHandler = RTCVideoEventHandlerRTS()

    var config = RTCVideoConfig(width = 720, height = 1280, fps = 15, bitrate = 1600)

    private var cameraId = CameraId.CAMERA_ID_FRONT

    private val engine: RTCVideo by lazy {
        RTCVideo.createRTCVideo(application, videoEventHandler, EngineConfig(appId)).also {
            it.setBusinessId(bid)

            it.setVideoCaptureConfig(config.asCaptureConfig())
            it.setVideoEncoderConfig(config.asEncoderConfig())

            it.switchCamera(cameraId)
        }
    }

    private val videoTranscoding: VideoTranscoding by lazy {
        RTCVideoTranscoding(engine)
    }

    private val _networkState = MutableStateFlow(NetworkState.NONE)
    override val networkState = _networkState.asStateFlow()

    override fun onCleared() {
        stopCapture()
        RTCVideo.destroyRTCVideo()

        super.onCleared()
    }

    override fun startVideoCapture() {
        engine.startVideoCapture()
    }

    override fun stopVideoCapture() {
        engine.stopVideoCapture()
    }

    override fun startAudioCapture() {
        engine.startAudioCapture()
    }

    override fun stopAudioCapture() {
        engine.stopAudioCapture()
    }

    override fun setLocalVideoView(view: TextureView?) {
        val canvas = view?.let { VideoCanvas(it, VideoCanvas.RENDER_MODE_HIDDEN) }
            ?: EmptyVideoCanvas
        engine.setLocalVideoCanvas(StreamIndex.STREAM_INDEX_MAIN, canvas)
    }

    override fun setLocalVideoView(view: SurfaceView?) {
        val canvas = view?.let { VideoCanvas(it, VideoCanvas.RENDER_MODE_HIDDEN) }
            ?: EmptyVideoCanvas
        engine.setLocalVideoCanvas(StreamIndex.STREAM_INDEX_MAIN, canvas)
    }

    override fun setRemoteVideoView(userId: String, view: SurfaceView?) {
        val roomId = rtcRoomId ?: run {
            Log.e(TAG, "setRemoteVideoView: rtcRoomId is null")
            return@setRemoteVideoView
        }
        val canvas = view?.let { VideoCanvas(it, VideoCanvas.RENDER_MODE_HIDDEN) }
            ?: EmptyVideoCanvas

        val streamKey = RemoteStreamKey(roomId, userId, StreamIndex.STREAM_INDEX_MAIN)
        engine.setRemoteVideoCanvas(streamKey, canvas)
    }

    override fun setRemoteVideoView(userId: String, view: TextureView?) {
        val roomId = rtcRoomId ?: run {
            Log.e(TAG, "setRemoteVideoView: rtcRoomId is null")
            return@setRemoteVideoView
        }

        val canvas = view?.let { VideoCanvas(it, VideoCanvas.RENDER_MODE_HIDDEN) }
            ?: EmptyVideoCanvas

        val streamKey = RemoteStreamKey(roomId, userId, StreamIndex.STREAM_INDEX_MAIN)
        engine.setRemoteVideoCanvas(streamKey, canvas)
    }

    override fun switchCamera() {
        cameraId = if (cameraId == CameraId.CAMERA_ID_FRONT) {
            CameraId.CAMERA_ID_BACK
        } else {
            CameraId.CAMERA_ID_FRONT
        }
        engine.switchCamera(cameraId)
    }

    private var rtsRoomId: String? = null
    private var rtsRoom: RTCRoom? = null
        set(value) {
            field?.destroy()
            field = value
        }

    private val rtsRoomEventHandler = object : RTCRoomEventHandlerRTS(
        notifyReconnect = true
    ) {
        override fun onRoomStateChanged(
            roomId: String,
            uid: String,
            state: Int,
            extraInfo: String
        ) {
            super.onRoomStateChanged(roomId, uid, state, extraInfo)
            Log.d(
                TAG,
                "[RTS]onRoomStateChanged: roomId: $roomId, uid: $uid, state: $state"
            )
        }
    }

    override fun joinChat(args: RoomArgs) {
        val roomId = args.rtsRoomId
        val userId = args.hostUserId
        val token = args.rtsToken

        rtsRoomId = roomId
        rtsRoom = engine.createRTCRoom(roomId).apply {
            setRTCRoomEventHandler(rtsRoomEventHandler)
        }.also {
            val userInfo = UserInfo(userId, "")

            // Only for RTS Messaging, so NO NEED to Publish and Subscribe
            val roomConfig = RTCRoomConfig(
                ChannelProfile.CHANNEL_PROFILE_LIVE_BROADCASTING,
                false,
                false,
                false
            )
            it.joinRoom(token, userInfo, roomConfig)
        }
    }

    fun leaveChat() {
        rtsRoom?.leaveRoom()
        rtsRoom = null
        rtsRoomId = null
    }

    private var rtcRoomId: String? = null
    private var rtcRoom: RTCRoom? = null
        set(value) {
            field?.destroy()
            field = value
        }

    @Volatile
    private var joinedRTC = false

    private var afterJoinRoom: (() -> Unit)? = null

    private fun ensureJoinRoom(roomId: String, userId: String, token: String, block: () -> Unit) {
        viewModelScope.launch(singleDispatcher) {
            if (joinedRTC) {
                block()
            } else {
                afterJoinRoom = block
                joinRTCRoom(roomId, userId, token)
            }
        }
    }

    private val rtcRoomEventHandler = object : RTCRoomEventHandlerRTS() {
        override fun onRoomStateChanged(
            roomId: String,
            uid: String,
            state: Int,
            extraInfo: String
        ) {
            super.onRoomStateChanged(roomId, uid, state, extraInfo)
            Log.d(
                TAG,
                "[RTC]onRoomStateChanged: roomId: $roomId, uid: $uid, state: $state"
            )

            viewModelScope.launch(singleDispatcher) {
                if (!joinedRTC) { // Only change to true
                    joinedRTC = (state == 0)
                }
                val command = afterJoinRoom ?: run {
                    Log.d(TAG, "No command to be executed!")
                    return@launch
                }
                afterJoinRoom = null
                if (isFirstJoinRoomSuccess(state, extraInfo)) {
                    command()
                } else {
                    Log.w(
                        TAG,
                        "onRoomStateChanged: isFirstJoinRoomSuccess=false, do not execute command!"
                    )
                }
            }
        }

        override fun onNetworkQuality(
            localQuality: NetworkQualityStats,
            remoteQualities: Array<out NetworkQualityStats>
        ) {
            super.onNetworkQuality(localQuality, remoteQualities)
            notifyNetworkState(localQuality)
        }
    }

    override fun joinRTCRoom(roomId: String, userId: String, token: String) {
        if (rtcRoom != null) {
            Log.d(
                TAG,
                "joinRTCRoom: is already joined: expect rtcRoomId: $roomId, actual: $rtcRoomId"
            )
            return
        }
        Log.d(
            TAG,
            "joinRTCRoom: roomId: $roomId, userId: $userId"
        )
        rtcRoomId = roomId
        rtcRoom = engine.createRTCRoom(roomId).apply {
            setRTCRoomEventHandler(rtcRoomEventHandler)
        }.also {
            val userInfo = UserInfo(userId, "")

            val roomConfig = RTCRoomConfig(
                ChannelProfile.CHANNEL_PROFILE_LIVE_BROADCASTING,
                true,
                true,
                true
            )
            it.joinRoom(token, userInfo, roomConfig)
        }
    }

    fun leaveRTCRoom() {
        viewModelScope.launch(singleDispatcher) { joinedRTC = false }
        rtcRoom?.leaveRoom()
        rtcRoom = null
        rtcRoomId = null
    }

    override fun startLive(args: RoomArgs) {
        val roomId = args.rtcRoomId
        val userId = args.hostUserId
        val token = args.rtcToken

        if (videoTranscoding.singleLiveNeedJoinRoom) {
            ensureJoinRoom(roomId, userId, token) {
                val command = VideoTranscoding.LiveCommand(
                    url = args.streamPushUrl,
                    config = config,
                    host = RTCVideoStream(roomId, userId)
                )
                videoTranscoding.execute(command)
            }
        } else {
            viewModelScope.launch(singleDispatcher) {
                val command = VideoTranscoding.LiveCommand(
                    url = args.streamPushUrl,
                    config = config,
                    host = RTCVideoStream(roomId, userId)
                )
                videoTranscoding.execute(command)
            }
        }
    }

    override fun stopLive() {
        viewModelScope.launch(singleDispatcher) {
            videoTranscoding.stop()
        }
        leaveChat()
        leaveRTCRoom()
    }

    override fun startAnchorLink(
        args: RoomArgs,
        forwardInfo: ForwardInfo,
        anchorUserId: String
    ) {
        val roomId = args.rtcRoomId
        val userId = args.hostUserId
        val token = args.rtcToken

        ensureJoinRoom(roomId, userId, token) {
            rtcRoom?.startForwardStreamToRooms(
                listOf(
                    ForwardStreamInfo(forwardInfo.roomId, forwardInfo.token)
                )
            )

            val command = VideoTranscoding.AnchorLinkCommand(
                url = args.streamPushUrl,
                config = config,
                host = RTCVideoStream(args.rtcRoomId, args.hostUserId),

                // Because anchor forward stream to host's room, so just use host's room id
                anchor = RTCVideoStream(args.rtcRoomId, anchorUserId),
            )
            videoTranscoding.execute(command)
        }
    }

    override fun stopAnchorLink(args: RoomArgs) {
        rtcRoom?.stopForwardStreamToRooms()

        val command = VideoTranscoding.LiveCommand(
            url = args.streamPushUrl,
            config = config,
            host = RTCVideoStream(args.rtcRoomId, args.hostUserId)
        )
        videoTranscoding.execute(command)

        if (!videoTranscoding.singleLiveNeedJoinRoom) {
            leaveRTCRoom()
        }
    }

    override fun startAudienceLink(args: RoomArgs, audienceUserIds: List<String>) {
        val roomId = args.rtcRoomId
        val userId = args.hostUserId
        val token = args.rtcToken

        ensureJoinRoom(roomId, userId, token) {
            val command = VideoTranscoding.AudienceLinkCommand(
                url = args.streamPushUrl,
                config = config,
                host = RTCVideoStream(args.rtcRoomId, args.hostUserId),
                audiences = audienceUserIds.map { RTCVideoStream(args.rtcRoomId, it) }
            )
            videoTranscoding.execute(command)
        }
    }

    override fun stopAudienceLink(args: RoomArgs) {
        val command = VideoTranscoding.LiveCommand(
            url = args.streamPushUrl,
            config = config,
            host = RTCVideoStream(args.rtcRoomId, args.hostUserId)
        )
        videoTranscoding.execute(command)

        if (!videoTranscoding.singleLiveNeedJoinRoom) {
            leaveRTCRoom()
        }
    }

    private fun notifyNetworkState(quality: NetworkQualityStats) {
        Log.d(TAG, "txQuality: $quality.txQuality")
        when (quality.txQuality) {
            NetworkQuality.NETWORK_QUALITY_EXCELLENT,
            NetworkQuality.NETWORK_QUALITY_GOOD -> {
                _networkState.value = NetworkState.GOOD
            }

            NetworkQuality.NETWORK_QUALITY_POOR,
            NetworkQuality.NETWORK_QUALITY_BAD,
            NetworkQuality.NETWORK_QUALITY_VERY_BAD -> {
                _networkState.value = NetworkState.BAD
            }

            NetworkQuality.NETWORK_QUALITY_DOWN -> {
                _networkState.value = NetworkState.DISCONNECTED
            }
        }
    }

    companion object {
        private const val TAG = "RTCEngineViewModel"

        val APP_ID_KEY = CreationExtras.Key<String>()
        val BID_KEY = CreationExtras.Key<String?>()

        val ENGINE_KEY = CreationExtras.Key<IRTCEngine>()

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY]!!
                val appId = this[APP_ID_KEY]!!
                val bid = this[BID_KEY] ?: ""
                RTCEngineViewModel(application, appId, bid)
            }
        }

        fun RTCVideoConfig.asCaptureConfig(): VideoCaptureConfig =
            VideoCaptureConfig(width, height, fps)

        fun RTCVideoConfig.asEncoderConfig(): VideoEncoderConfig =
            VideoEncoderConfig(width, height, fps, bitrate, 0)
    }
}

// Immutable
@SuppressLint("StaticFieldLeak")
val EmptyVideoCanvas = VideoCanvas()