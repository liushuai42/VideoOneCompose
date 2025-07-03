package com.vertcdemo.solution.interactivelive.core.events

import com.vertcdemo.base.utils.json
import com.vertcdemo.solution.interactivelive.core.annotation.LiveFinishType
import com.vertcdemo.solution.interactivelive.core.annotation.RoomStatus
import com.vertcdemo.solution.interactivelive.network.data.LiveSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomStatusEvent(
    @RoomStatus
    @SerialName("linkmic_status") val status: Int
)

@Serializable
data class LiveFinishEvent(
    @SerialName("room_id") val roomId: String,
    @LiveFinishType
    @SerialName("type") val type: Int = LiveFinishType.NORMAL,
    @SerialName("extra") val extra: String?
) {
    val liveSummary: LiveSummary
        get() {
            if (extra == null) {
                return LiveSummary()
            }

            return json.decodeFromString(extra)
        }
}