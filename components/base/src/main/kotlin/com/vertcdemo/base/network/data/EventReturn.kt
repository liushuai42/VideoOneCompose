package com.vertcdemo.base.network.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

@Serializable
class EventReturn(
    @JsonNames("code")
    var code: Int,
    @JsonNames("message")
    var message: String,
    @JsonNames("response")
    var response: JsonElement?
) {
    fun string(): String? = response?.toString()
}