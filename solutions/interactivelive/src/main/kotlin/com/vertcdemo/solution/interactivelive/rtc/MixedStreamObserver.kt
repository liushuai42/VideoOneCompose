package com.vertcdemo.solution.interactivelive.rtc

import android.util.Log
import com.ss.bytertc.engine.live.ByteRTCStreamMixingEvent
import com.ss.bytertc.engine.live.ByteRTCTranscoderErrorCode
import com.ss.bytertc.engine.live.IMixedStreamObserver
import com.ss.bytertc.engine.live.MixedStreamType
import com.ss.bytertc.engine.video.VideoFrame

private const val TAG = "MixedStreamObserver"

class MixedStreamObserver : IMixedStreamObserver {
    override fun isSupportClientPushStream() = false

    override fun onMixingEvent(
        eventType: ByteRTCStreamMixingEvent?,
        taskId: String?,
        error: ByteRTCTranscoderErrorCode?,
        mixType: MixedStreamType?
    ) {
        Log.d(
            TAG,
            "onMixingEvent: eventType=$eventType; error=$error; mixType=$mixType"
        )
    }

    override fun onMixingAudioFrame(
        taskId: String?,
        audioFrame: ByteArray?,
        frameNum: Int,
        timeStampMs: Long
    ) {
    }

    override fun onMixingVideoFrame(
        taskId: String?,
        videoFrame: VideoFrame?
    ) {
    }

    override fun onMixingDataFrame(
        taskId: String?,
        dataFrame: ByteArray?,
        time: Long
    ) {
    }

    override fun onCacheSyncVideoFrames(
        taskId: String?,
        userIds: Array<out String?>?,
        videoFrame: Array<out VideoFrame?>?,
        dataFrame: Array<out ByteArray?>?,
        count: Int
    ) {
    }
}