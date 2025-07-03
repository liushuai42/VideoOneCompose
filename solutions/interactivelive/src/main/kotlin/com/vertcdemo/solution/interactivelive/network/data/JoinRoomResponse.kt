package com.vertcdemo.solution.interactivelive.network.data

import com.vertcdemo.solution.interactivelive.core.annotation.LiveLinkMicStatus
import com.vertcdemo.solution.interactivelive.core.annotation.RoomStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
class JoinRoomResponse(
    @SerialName("user_info")
    val liveUserInfo: LiveUserInfo,
    @SerialName("host_user_info")
    val liveHostUserInfo: LiveUserInfo,
    @SerialName("live_room_info")
    val liveRoomInfo: LiveRoomInfo,
    @SerialName("rts_token")
    @JsonNames("rtm_token")
    val rtsToken: String,
) {
    @RoomStatus
    val roomStatus: Int
        get() = when (liveHostUserInfo.linkMicStatus) {
            LiveLinkMicStatus.AUDIENCE_INTERACTING -> RoomStatus.AUDIENCE_LINK
            LiveLinkMicStatus.HOST_INTERACTING -> RoomStatus.PK
            else -> RoomStatus.LIVE
        }
}

