package com.vertcdemo.base.network.data

import com.vertcdemo.base.R
import com.vertcdemo.base.utils.ApplicationProvider.applicationContext
import com.vertcdemo.base.utils.SolutionDataManager
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EventBody(
    @SerialName("event_name")
    val eventName: String,
    @SerialName("content")
    val content: String,
    @SerialName("device_id")
    val deviceId: String = SolutionDataManager.ins().deviceId,
    @SerialName("language")
    val language: String = applicationContext.getString(R.string.language_code)
)