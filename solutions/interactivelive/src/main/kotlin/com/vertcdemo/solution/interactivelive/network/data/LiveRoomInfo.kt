package com.vertcdemo.solution.interactivelive.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LiveRoomInfo(
    @SerialName("room_id")
    val roomId: String,
    @SerialName("host_user_id")
    val hostUserId: String,
    @SerialName("host_user_name")
    val hostUsername: String,
    @SerialName("audience_count")
    val audienceCount: Int = 0,
    @SerialName("stream_pull_url_list")
    val streamPullStreamList: Map<String, String> = emptyMap(),
    @SerialName("extra")
    val extra: String? = null,
)

val FakeLiveRoomInfo = LiveRoomInfo(
    roomId = "10086",
    hostUserId = "42",
    hostUsername = "John Doe",
    audienceCount = 42,
)
