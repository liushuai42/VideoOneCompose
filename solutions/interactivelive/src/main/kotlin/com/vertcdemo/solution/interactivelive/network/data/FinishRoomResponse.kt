package com.vertcdemo.solution.interactivelive.network.data

import android.util.Base64
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.vertcdemo.base.utils.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FinishRoomResponse(
    @SerialName("live_room_info")
    val liveRoomInfo: LiveRoomInfo
) {
    val liveSummary: LiveSummary
        get() {
            val extras = liveRoomInfo.extra ?: return LiveSummary()
            return json.decodeFromString(extras)
        }
}

@Serializable
data class LiveSummary(
    @SerialName("duration")
    val duration: Long = 0,
    @SerialName("viewers")
    val viewers: Int = 0,
    @SerialName("likes")
    val likes: Int = 0,
    @SerialName("gifts")
    val gifts: Int = 0,
)

object LiveSummaryNavType : NavType<LiveSummary>(isNullableAllowed = false) {
    override fun put(bundle: SavedState, key: String, value: LiveSummary) {
        bundle.putString(key, serializeAsValue(value))
    }

    override fun get(bundle: SavedState, key: String): LiveSummary? {
        return bundle.getString(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): LiveSummary {
        return json.decodeFromString(
            String(Base64.decode(value, Base64.URL_SAFE))
        )
    }

    override fun serializeAsValue(value: LiveSummary): String {
        return Base64.encodeToString(
            json.encodeToString(value).toByteArray(),
            Base64.URL_SAFE
        )
    }
}