package com.vertcdemo.solution.interactivelive.rtc

import com.ss.bytertc.engine.RTCVideo
import com.ss.bytertc.engine.live.MixedStreamConfig
import com.vertcdemo.solution.interactivelive.core.RTCVideoConfig

const val KEY_LIVE_MODE = "liveMode"

const val MAX_AUDIENCE_COUNT = 6

interface VideoTranscoding {
    sealed class Command(
        val url: String,
        val config: RTCVideoConfig,
        val host: RTCVideoStream,
    )

    class LiveCommand(
        url: String,
        config: RTCVideoConfig,
        host: RTCVideoStream,
    ) : Command(url, config, host) {
        constructor(command: Command) : this(command.url, command.config, command.host)
    }

    class AudienceLinkCommand(
        url: String,
        config: RTCVideoConfig,
        host: RTCVideoStream,
        val audiences: List<RTCVideoStream>
    ) : Command(url, config, host)

    class AnchorLinkCommand(
        url: String,
        config: RTCVideoConfig,
        host: RTCVideoStream,
        val anchor: RTCVideoStream,
    ) : Command(url, config, host)

    /**
     * 单直播时是否需要 RTC 房间
     */
    val singleLiveNeedJoinRoom: Boolean

    fun execute(command: Command)

    fun stop()
}

class RTCVideoTranscoding(private val engine: RTCVideo) : VideoTranscoding {
    private val taskId = ""
    private val observer = MixedStreamObserver()

    private var isRTCTranscoding = false

    override val singleLiveNeedJoinRoom: Boolean = true

    override fun execute(command: VideoTranscoding.Command) {
        val config: MixedStreamConfig = when (command) {
            is VideoTranscoding.AudienceLinkCommand -> {

                val audiences = if (command.audiences.size > MAX_AUDIENCE_COUNT) {
                    command.audiences.subList(0, MAX_AUDIENCE_COUNT)
                } else {
                    command.audiences
                }

                if (audiences.isEmpty()) {
                    singleLiveConfig(
                        url = command.url,
                        config = command.config,
                        host = command.host,
                    )
                } else if (audiences.size == 1) {
                    audienceLink1v1Config(
                        url = command.url,
                        config = command.config,
                        host = command.host,
                        audience = audiences.first()
                    )
                } else {
                    audienceLink1vNConfig(
                        url = command.url,
                        config = command.config,
                        host = command.host,
                        audiences = audiences
                    )
                }
            }

            is VideoTranscoding.AnchorLinkCommand -> {
                anchorLink1v1Config(
                    url = command.url,
                    config = command.config,
                    host = command.host,
                    anchor = command.anchor
                )
            }

            is VideoTranscoding.LiveCommand -> {
                singleLiveConfig(
                    url = command.url,
                    config = command.config,
                    host = command.host,
                )
            }
        }

        startOrUpdate(config)
    }

    override fun stop() {
        isRTCTranscoding = false
        engine.stopPushStreamToCDN(taskId)
    }

    private fun startOrUpdate(config: MixedStreamConfig) {
        if (isRTCTranscoding) {
            engine.updatePushMixedStreamToCDN(taskId, config)
        } else {
            isRTCTranscoding = true
            engine.startPushMixedStreamToCDN(taskId, config, observer)
        }
    }
}


data class RTCVideoStream(
    val roomId: String,
    val userId: String,
)
