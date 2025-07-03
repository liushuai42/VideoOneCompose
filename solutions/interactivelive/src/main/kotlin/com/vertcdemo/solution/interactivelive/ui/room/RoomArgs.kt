package com.vertcdemo.solution.interactivelive.ui.room

import android.util.Base64
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.vertcdemo.base.utils.json
import com.vertcdemo.solution.interactivelive.network.data.FakeLiveRoomInfo
import com.vertcdemo.solution.interactivelive.network.data.FakeLiveUserInfo
import com.vertcdemo.solution.interactivelive.network.data.LiveRoomInfo
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomArgs(
    @SerialName("room_info")
    val roomInfo: LiveRoomInfo,
    @SerialName("user_info")
    val userInfo: LiveUserInfo,
    @SerialName("stream_push_url")
    val streamPushUrl: String,
    @SerialName("rts_token")
    val rtsToken: String,
    @SerialName("rtc_token")
    val rtcToken: String,
    @SerialName("rtc_room_id")
    val rtcRoomId: String,
) {
    val rtsRoomId: String
        get() = roomInfo.roomId

    val hostUserId: String
        get() = roomInfo.hostUserId
}

object RoomArgsNavType : NavType<RoomArgs>(isNullableAllowed = false) {
    override fun put(bundle: SavedState, key: String, value: RoomArgs) {
        bundle.putString(key, serializeAsValue(value))
    }

    override fun get(bundle: SavedState, key: String): RoomArgs? {
        return bundle.getString(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): RoomArgs {
        return json.decodeFromString(
            String(Base64.decode(value, Base64.URL_SAFE))
        )
    }

    override fun serializeAsValue(value: RoomArgs): String {
        return Base64.encodeToString(
            json.encodeToString(value).toByteArray(),
            Base64.URL_SAFE
        )
    }
}

val FakeRoomArgs = RoomArgs(
    roomInfo = FakeLiveRoomInfo,
    userInfo = FakeLiveUserInfo,
    streamPushUrl = "https://pushing.examples.com/videoone-x.rtmp",
    rtsToken = "fake_token",
    rtcToken = "fake_token",
    rtcRoomId = "43",
)
