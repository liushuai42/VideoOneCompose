package com.vertcdemo.solution.interactivelive.core.events

import com.vertcdemo.solution.interactivelive.network.data.FakeLiveUserInfo
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnchorLinkInviteEvent(
    @SerialName("inviter") val userInfo: LiveUserInfo,
    @SerialName("linker_id") val linkerId: String,
    @SerialName("extra") val extra: String,
)

@Serializable
data class AnchorLinkReplyEvent(
    @SerialName("invitee") val userInfo: LiveUserInfo,
    @SerialName("linker_id") val linkerId: String,
    @SerialName("reply_type") val replyType: Int,
    @SerialName("rtc_room_id") val rtcRoomId: String,
    @SerialName("rtc_token") val rtcToken: String,
    @SerialName("rtc_user_list") val rtcUserList: List<LiveUserInfo>?,
)


@Serializable
data class AnchorLinkFinishEvent(
    @SerialName("rtc_room_id") val rtcRoomId: String,
)

val FakeAnchorLinkInviteEvent = AnchorLinkInviteEvent(
    userInfo = FakeLiveUserInfo,
    linkerId = "linker_id",
    extra = "extra",
)
