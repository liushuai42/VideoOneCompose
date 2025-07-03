package com.vertcdemo.solution.interactivelive.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PermitAudienceLinkResponse(
    @SerialName("linker_id")
    val linkerId: String,
    @SerialName("rtc_room_id")
    val rtcRoomId: String,
    @SerialName("rtc_token")
    val rtcToken: String,
    @SerialName("rtc_user_list")
    val userList: List<LiveUserInfo>?
)