package com.vertcdemo.base.network.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
class RTCAppInfoResponse(
    @JsonNames("app_id")
    val appId: String,
    @JsonNames("bid")
    val bid: String,
)