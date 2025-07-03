package com.vertcdemo.solution.interactivelive.network.data

import com.vertcdemo.solution.interactivelive.core.annotation.LiveLinkMicStatus
import com.vertcdemo.solution.interactivelive.core.annotation.RoomStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReconnectResponse(
    @SerialName("user")
    val userInfo: LiveUserInfo,
    @SerialName("reconnect_info")
    val reconnectInfo: ReconnectInfo,
    @SerialName("linkmic_status")
    @LiveLinkMicStatus
    val linkMicStatus: Int,
) {
    val roomStatus: Int = when (linkMicStatus) {
        LiveLinkMicStatus.AUDIENCE_INTERACTING -> RoomStatus.AUDIENCE_LINK
        LiveLinkMicStatus.HOST_INTERACTING -> RoomStatus.PK
        else -> RoomStatus.LIVE
    }
}

@Serializable
data class ReconnectInfo(
    @SerialName("live_room_info")
    val liveRoomInfo: LiveRoomInfo,
    @SerialName("audience_count")
    val audienceCount: Int,
    @SerialName("stream_push_url")
    val streamPushUrl: String,
    @SerialName("stream_pull_url")
    val streamPullUrl: Map<String, String>,
    @SerialName("rtc_room_id")
    val rtcRoomId: String,
    @SerialName("rtc_token")
    val rtcToken: String,
    @SerialName("linkmic_user_list")
    val linkMicUsers: List<LiveUserInfo> = emptyList(),
    @SerialName("linker_id")
    val linkerId: String = "",
)
