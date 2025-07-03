package com.vertcdemo.solution.interactivelive.core.events

import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AudienceLinkStatusEvent {
    @Serializable
    data class Join(
        @SerialName("rtc_room_id") val rtcRoomId: String,
        @SerialName("user_list") val userList: List<LiveUserInfo> = emptyList(),
        @SerialName("user_id") val userId: String,
    ) : AudienceLinkStatusEvent

    @Serializable
    data class Leave(
        @SerialName("rtc_room_id") val rtcRoomId: String,
        @SerialName("user_list") val userList: List<LiveUserInfo> = emptyList(),
        @SerialName("user_id") val userId: String,
    ) : AudienceLinkStatusEvent
}


@Serializable
data class AudienceLinkFinishEvent(
    @SerialName("rtc_room_id") val rtcRoomId: String,
)

@Serializable
data class AudienceLinkInviteEvent(
    @SerialName("inviter") val userInfo: LiveUserInfo,
    @SerialName("linker_id") val linkerId: String,
    @SerialName("extra") val extra: String,
)

@Serializable
data class AudienceLinkApplyEvent(
    @SerialName("applicant") val applicant: LiveUserInfo,
    @SerialName("linker_id") val linkerId: String,
    @SerialName("extra") val extra: String,
) {
    fun isUser(userId: String): Boolean {
        return applicant.userId == userId
    }

    val userId: String
        get() = applicant.userId
}

@Serializable
data class AudienceLinkCancelEvent(
    @SerialName("rtc_room_id") val rtcRoomId: String,
    @SerialName("user_id") val userId: String,
)

@Serializable
data class AudienceLinkReplyEvent(
    @SerialName("invitee") val userInfo: LiveUserInfo,
    @SerialName("linker_id") val linkerId: String,
    @SerialName("reply_type") val replyType: Int,
    @SerialName("rtc_room_id") val rtcRoomId: String,
    @SerialName("rtc_token") val rtcToken: String,
    @SerialName("rtc_user_list") val rtcUserList: List<LiveUserInfo> = emptyList(),
)

@Serializable
data class AudienceLinkPermitEvent(
    @SerialName("linker_id") val linkerId: String,
    @SerialName("permit_type") val permitType: Int,
    @SerialName("rtc_room_id") val rtcRoomId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("rtc_token") val rtcToken: String,
    @SerialName("rtc_user_list") val rtcUserList: List<LiveUserInfo> = emptyList(),
)

@Serializable
data class AudienceLinkKickEvent(
    @SerialName("linker_id") val linkerId: String,
    @SerialName("rtc_room_id") val rtcRoomId: String,
    @SerialName("room_id") val rtsRoomId: String,
    @SerialName("user_id") val userId: String,
)

@Serializable
data class AudienceLinkKickResultEvent(@SerialName("user_id") val userId: String)
