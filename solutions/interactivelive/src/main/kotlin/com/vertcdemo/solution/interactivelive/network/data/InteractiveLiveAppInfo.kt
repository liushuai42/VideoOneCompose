package com.vertcdemo.solution.interactivelive.network.data

import com.vertcdemo.base.network.data.RTCAppInfoRequest
import com.vertcdemo.solution.interactivelive.BuildConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class InteractiveLiveAppInfo(
    @SerialName("scenes_name")
    val sceneName: String = "live",
    @SerialName("live_pull_domain")
    val livePullDomain: String? = BuildConfig.LIVE_PULL_DOMAIN.ifEmpty { null },
    @SerialName("live_push_domain")
    val livePushDomain: String? = BuildConfig.LIVE_PUSH_DOMAIN.ifEmpty { null },
    @SerialName("live_push_key")
    val livePushKey: String? = BuildConfig.LIVE_PUSH_KEY.ifEmpty { null },
    @SerialName("live_app_name")
    val liveAppName: String? = BuildConfig.LIVE_APP_NAME.ifEmpty { null },
) : RTCAppInfoRequest()