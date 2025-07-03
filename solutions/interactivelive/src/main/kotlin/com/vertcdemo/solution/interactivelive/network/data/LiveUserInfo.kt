package com.vertcdemo.solution.interactivelive.network.data

import com.vertcdemo.solution.interactivelive.core.annotation.LiveLinkMicStatus
import com.vertcdemo.solution.interactivelive.core.annotation.LiveRoleType
import com.vertcdemo.solution.interactivelive.core.annotation.MediaStatus
import com.vertcdemo.solution.interactivelive.core.events.UserMediaChangedEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveUserInfo(
    @SerialName("room_id")
    val roomId: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("user_name")
    val userName: String,

    @SerialName("user_role")
    @LiveRoleType
    val role: Int = LiveRoleType.AUDIENCE,

    @MediaStatus
    @SerialName("mic")
    val mic: Int = MediaStatus.OFF,

    @MediaStatus
    @SerialName("camera")
    val camera: Int = MediaStatus.OFF,

    /**
     * Additional information storage width and height
     * Format: "{\"width\":0,\"height\":0}"
     */
    @SerialName("extra")
    val extra: String? = "",

    @LiveLinkMicStatus
    @SerialName("linkmic_status")
    val linkMicStatus: Int = LiveLinkMicStatus.OTHER
) {
    fun copyFrom(event: UserMediaChangedEvent) =
        if (event.mic == MediaStatus.KEEP && event.camera == MediaStatus.KEEP) {
            this
        } else if (event.mic == MediaStatus.KEEP) {
            this.copy(camera = event.camera)
        } else if (event.camera == MediaStatus.KEEP) {
            this.copy(mic = event.mic)
        } else {
            this.copy(mic = event.mic, camera = event.camera)
        }

    val isMicOn: Boolean
        get() = mic == MediaStatus.ON

    val isCameraOn: Boolean
        get() = camera == MediaStatus.ON
}

val FakeLiveUserInfo = LiveUserInfo(
    roomId = "10086",
    userId = "42",
    userName = "John Doe",
)