package com.vertcdemo.solution.interactivelive.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetAudienceListResponse(
    @SerialName("audience_list")
    val audienceList: List<LiveUserInfo> = emptyList(),
)