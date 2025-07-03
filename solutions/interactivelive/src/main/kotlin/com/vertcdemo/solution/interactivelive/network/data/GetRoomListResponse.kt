package com.vertcdemo.solution.interactivelive.network.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
class GetRoomListResponse(
    @JsonNames("live_room_list")
    val rooms: List<LiveRoomInfo> = emptyList()
)