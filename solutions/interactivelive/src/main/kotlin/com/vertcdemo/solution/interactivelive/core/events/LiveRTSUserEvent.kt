package com.vertcdemo.solution.interactivelive.core.events

import com.vertcdemo.solution.interactivelive.core.annotation.MediaStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface LiveRTSUserEvent {
    val audienceUserId: String
    val audienceUserName: String
    val audienceCount: Int

    @Serializable
    data class Join(
        @SerialName("audience_user_id")
        override val audienceUserId: String,
        @SerialName("audience_user_name")
        override val audienceUserName: String,
        @SerialName("audience_count")
        override val audienceCount: Int
    ) : LiveRTSUserEvent

    @Serializable
    data class Leave(
        @SerialName("audience_user_id")
        override val audienceUserId: String,
        @SerialName("audience_user_name")
        override val audienceUserName: String,
        @SerialName("audience_count")
        override val audienceCount: Int
    ) : LiveRTSUserEvent
}

@Serializable
data class UserMediaChangedEvent(
    @SerialName("room_id") val rtcRoomId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("operator_user_id") val operatorId: String,
    @field:MediaStatus
    @SerialName("camera") val camera: Int = MediaStatus.OFF,
    @field:MediaStatus
    @SerialName("mic") val mic: Int = MediaStatus.OFF,
)

@Serializable
data class UserMediaControlEvent(
    @SerialName("guest_room_id") val guestRoomId: String,
    @SerialName("guest_user_id") val guestUserId: String,
    @field:MediaStatus
    @SerialName("camera") val camera: Int = MediaStatus.OFF,
    @field:MediaStatus
    @SerialName("mic") val mic: Int = MediaStatus.OFF,
)