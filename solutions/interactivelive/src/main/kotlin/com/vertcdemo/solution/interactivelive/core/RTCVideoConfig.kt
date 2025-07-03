package com.vertcdemo.solution.interactivelive.core

data class RTCVideoConfig(
    val width: Int = 720,
    val height: Int = 1280,
    val fps: Int = 15,
    val bitrate: Int = 1600,
)