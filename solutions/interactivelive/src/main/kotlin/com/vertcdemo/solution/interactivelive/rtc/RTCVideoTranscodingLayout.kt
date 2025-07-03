package com.vertcdemo.solution.interactivelive.rtc

import com.ss.bytertc.engine.live.ByteRTCStreamMixingType
import com.ss.bytertc.engine.live.MixedStreamConfig
import com.vertcdemo.base.utils.json
import com.vertcdemo.solution.interactivelive.core.RTCVideoConfig
import com.vertcdemo.solution.interactivelive.core.annotation.LiveMode


fun singleLiveConfig(
    url: String,
    config: RTCVideoConfig,
    host: RTCVideoStream,
): MixedStreamConfig {
    val mixedConfig = MixedStreamConfig.defaultMixedStreamConfig()
        .setRoomID(host.roomId)
        .setPushURL(url)
        .setExpectedMixingType(ByteRTCStreamMixingType.STREAM_MIXING_BY_SERVER)

    // Set the live transcoding audio parameters, the specific parameters depend on the situation
    mixedConfig.audioConfig.let {
        it.setSampleRate(44100)
        it.setChannels(2)
    }

    val videoWidth = config.width
    val videoHeight = config.height
    val frameRate = config.fps
    val bitrate = config.bitrate

    mixedConfig.videoConfig.let {
        it.setWidth(videoWidth)
        it.setHeight(videoHeight)
        it.setFps(frameRate)
        it.setBitrate(bitrate)
    }

    // Set live transcoding video layout parameters
    val region = MixedStreamConfig.MixedStreamLayoutRegionConfig()
        .setRoomID(host.roomId)
        .setUserID(host.userId)
        .setIsLocalUser(true)
        .setLocationX(0)
        .setLocationY(0)
        .setWidth(videoWidth)
        .setHeight(videoHeight)
        .setAlpha(1.0)
        .setZOrder(0)
        .setRenderMode(MixedStreamConfig.MixedStreamRenderMode.MIXED_STREAM_RENDER_MODE_HIDDEN)

    mixedConfig.setLayout(
        MixedStreamConfig.MixedStreamLayoutConfig()
            .setRegions(arrayOf(region))
            .setUserConfigExtraInfo(appData(LiveMode.NORMAL))
    )

    return mixedConfig
}

fun audienceLink1v1Config(
    url: String,
    config: RTCVideoConfig,
    host: RTCVideoStream,
    audience: RTCVideoStream
): MixedStreamConfig {
    val mixedConfig = MixedStreamConfig.defaultMixedStreamConfig()
        .setRoomID(host.roomId)
        .setPushURL(url)
        .setExpectedMixingType(ByteRTCStreamMixingType.STREAM_MIXING_BY_SERVER)

    // Set the live transcoding audio parameters, the specific parameters depend on the situation
    mixedConfig.audioConfig.let {
        it.setSampleRate(44100)
        it.setChannels(2)
    }

    val videoWidth = config.width
    val videoHeight = config.height
    val frameRate = config.fps
    val bitrate = config.bitrate

    mixedConfig.videoConfig.let {
        it.setWidth(videoWidth)
        it.setHeight(videoHeight)
        it.setFps(frameRate)
        it.setBitrate(bitrate)
    }

    // Set live transcoding video layout parameters
    val regions = mutableListOf<MixedStreamConfig.MixedStreamLayoutRegionConfig>()

    regions += MixedStreamConfig.MixedStreamLayoutRegionConfig()
        .setRoomID(host.roomId)
        .setUserID(host.userId)
        .setIsLocalUser(true)
        .setLocationX(0)
        .setLocationY(0)
        .setWidth(videoWidth)
        .setHeight(videoHeight)
        .setAlpha(1.0)
        .setZOrder(0)
        .setRenderMode(MixedStreamConfig.MixedStreamRenderMode.MIXED_STREAM_RENDER_MODE_HIDDEN)

    // Figma UI Size
    val screenWidth = 365.0
    val screenHeight = 667.0
    val itemSize = 120.0
    val itemRightSpace = 24.0
    val itemBottomSpace = 52.0
    val itemCornerRadius = 2.0

    val ratio = videoWidth / screenWidth

    val cornerRadius = itemCornerRadius / itemSize

    regions += MixedStreamConfig.MixedStreamLayoutRegionConfig()
        .setRoomID(audience.roomId)
        .setUserID(audience.userId)
        .setLocationX(videoWidth - ((itemSize + itemRightSpace) * ratio).toInt())
        .setLocationY(videoHeight - ((itemSize + itemBottomSpace) * ratio).toInt())
        .setWidth((itemSize * ratio).toInt())
        .setHeight((itemSize * ratio).toInt())
        .setCornerRadius(cornerRadius)
        .setAlpha(1.0)
        .setZOrder(1)

    mixedConfig.setLayout(
        MixedStreamConfig.MixedStreamLayoutConfig()
            .setRegions(regions.toTypedArray())
            .setUserConfigExtraInfo(appData(LiveMode.LINK_1v1))
    )

    return mixedConfig
}

fun audienceLink1vNConfig(
    url: String,
    config: RTCVideoConfig,
    host: RTCVideoStream,
    audiences: List<RTCVideoStream>,
): MixedStreamConfig {
    val mixedConfig = MixedStreamConfig.defaultMixedStreamConfig()
        .setRoomID(host.roomId)
        .setPushURL(url)
        .setExpectedMixingType(ByteRTCStreamMixingType.STREAM_MIXING_BY_SERVER)

    // Set the live transcoding audio parameters, the specific parameters depend on the situation
    mixedConfig.audioConfig.let {
        it.setSampleRate(44100)
        it.setChannels(2)
    }

    val videoWidth = config.width
    val videoHeight = config.height
    val frameRate = config.fps
    val bitrate = config.bitrate

    mixedConfig.videoConfig.let {
        it.setWidth(videoWidth)
        it.setHeight(videoHeight)
        it.setFps(frameRate)
        it.setBitrate(bitrate)
    }

    // Set live transcoding video layout parameters
    val regions = mutableListOf<MixedStreamConfig.MixedStreamLayoutRegionConfig>()

    // Figma UI Size
    val edgePixels = 4

    val itemSizePixels = (videoHeight - edgePixels * 5) / MAX_AUDIENCE_COUNT // floor

    regions += MixedStreamConfig.MixedStreamLayoutRegionConfig()
        .setRoomID(host.roomId)
        .setUserID(host.userId)
        .setIsLocalUser(true)
        .setLocationX(0)
        .setLocationY(0)
        .setWidth(videoWidth - itemSizePixels - edgePixels)
        .setHeight(videoHeight)
        .setAlpha(1.0)
        .setZOrder(0)
        .setRenderMode(MixedStreamConfig.MixedStreamRenderMode.MIXED_STREAM_RENDER_MODE_HIDDEN)

    val locationX = videoWidth - itemSizePixels

    regions += audiences.mapIndexed { index, user ->
        MixedStreamConfig.MixedStreamLayoutRegionConfig()
            .setRoomID(user.roomId)
            .setUserID(user.userId)
            .setIsLocalUser(false)
            .setLocationX(locationX)
            .setLocationY((itemSizePixels + edgePixels) * index)
            .setWidth(itemSizePixels)
            .setHeight(itemSizePixels)
            .setAlpha(1.0)
            .setZOrder(1)
            .setRenderMode(MixedStreamConfig.MixedStreamRenderMode.MIXED_STREAM_RENDER_MODE_HIDDEN)
    }

    mixedConfig.setLayout(
        MixedStreamConfig.MixedStreamLayoutConfig()
            .setBackgroundColor("#0D0B53")
            .setRegions(regions.toTypedArray())
            .setUserConfigExtraInfo(appData(LiveMode.LINK_1vN))
    )

    return mixedConfig
}

fun anchorLink1v1Config(
    url: String,
    config: RTCVideoConfig,
    host: RTCVideoStream,
    anchor: RTCVideoStream
): MixedStreamConfig {
    val mixedConfig = MixedStreamConfig.defaultMixedStreamConfig()
        .setRoomID(host.roomId)
        .setPushURL(url)
        .setExpectedMixingType(ByteRTCStreamMixingType.STREAM_MIXING_BY_SERVER)

    // Set the live transcoding audio parameters, the specific parameters depend on the situation
    mixedConfig.audioConfig.let {
        it.setSampleRate(44100)
        it.setChannels(2)
    }

    val videoWidth = config.width
    val videoHeight = config.height
    val frameRate = config.fps
    val bitrate = config.bitrate

    mixedConfig.videoConfig.let {
        it.setWidth(videoWidth)
        it.setHeight(videoHeight)
        it.setFps(frameRate)
        it.setBitrate(bitrate)
    }

    // Set live transcoding video layout parameters
    val regions = mutableListOf<MixedStreamConfig.MixedStreamLayoutRegionConfig>()

    val halfVideoWidth = (videoWidth * 0.5).toInt()
    val halfVideoHeight = (videoHeight * 0.5).toInt()
    val quarterVideoHeight = (videoHeight * 0.25).toInt()

    regions += MixedStreamConfig.MixedStreamLayoutRegionConfig()
        .setRoomID(host.roomId)
        .setUserID(host.userId)
        .setIsLocalUser(true)
        .setLocationX(0)
        .setLocationY(quarterVideoHeight)
        .setWidth(halfVideoWidth)
        .setHeight(halfVideoHeight)
        .setAlpha(1.0)
        .setZOrder(0)
        .setRenderMode(MixedStreamConfig.MixedStreamRenderMode.MIXED_STREAM_RENDER_MODE_HIDDEN)

    regions += MixedStreamConfig.MixedStreamLayoutRegionConfig()
        .setRoomID(anchor.roomId)
        .setUserID(anchor.userId)
        .setIsLocalUser(true)
        .setLocationX(halfVideoWidth)
        .setLocationY(quarterVideoHeight)
        .setWidth(halfVideoWidth)
        .setHeight(halfVideoHeight)
        .setAlpha(1.0)
        .setZOrder(0)
        .setRenderMode(MixedStreamConfig.MixedStreamRenderMode.MIXED_STREAM_RENDER_MODE_HIDDEN)

    mixedConfig.setLayout(
        MixedStreamConfig.MixedStreamLayoutConfig()
            .setRegions(regions.toTypedArray())
            .setUserConfigExtraInfo(appData(LiveMode.LINK_PK))
    )

    return mixedConfig
}

private fun appData(@LiveMode liveMode: Int): String {
    val data = KEY_LIVE_MODE to liveMode
    return json.encodeToString(data)
}