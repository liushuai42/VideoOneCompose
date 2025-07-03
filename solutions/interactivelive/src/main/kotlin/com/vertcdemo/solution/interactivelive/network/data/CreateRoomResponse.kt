package com.vertcdemo.solution.interactivelive.network.data

import com.vertcdemo.solution.interactivelive.ui.room.RoomArgs
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
class CreateRoomResponse(
    @SerialName("live_room_info")
    val liveRoomInfo: LiveRoomInfo,

    @SerialName("user_info")
    val userInfo: LiveUserInfo,

    @SerialName("stream_push_url")
    val streamPushUrl: String,

    @SerialName("rts_token")
    @JsonNames("rtm_token")
    val rtsToken: String,

    @SerialName("rtc_token")
    val rtcToken: String,

    @SerialName("rtc_room_id")
    val rtcRoomId: String,
) {
    fun toRoomArgs(): RoomArgs = RoomArgs(
        roomInfo = liveRoomInfo,
        userInfo = userInfo,
        streamPushUrl = streamPushUrl,
        rtsToken = rtsToken,
        rtcToken = rtcToken,
        rtcRoomId = rtcRoomId,
    )
}