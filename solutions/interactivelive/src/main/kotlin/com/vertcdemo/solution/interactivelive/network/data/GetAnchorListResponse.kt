package com.vertcdemo.solution.interactivelive.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetAnchorListResponse(
    @SerialName("anchor_list")
    val anchorList: List<LiveUserInfo> = emptyList(),
)