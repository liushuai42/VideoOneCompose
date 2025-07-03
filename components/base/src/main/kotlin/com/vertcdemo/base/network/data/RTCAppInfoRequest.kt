package com.vertcdemo.base.network.data

import android.util.Log
import com.vertcdemo.base.BuildConfig.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class RTCAppInfoRequest {

    @SerialName("app_id")
    val appId: String?

    @SerialName("app_key")
    val appKey: String?

    @SerialName("access_key")
    val accessKey: String?

    @SerialName("secret_access_key")
    val secretAccessKey: String?

    init {
        if (APP_ID.isNotEmpty()) {
            Log.d(TAG, "joinRTS: APP_ID found. Switch client mode.")

            require(APP_KEY.isNotEmpty()) { "AppKey is empty" }
            require(ACCESS_KEY_ID.isNotEmpty()) { "AccessKeyID is empty" }
            require(SECRET_ACCESS_KEY.isNotEmpty()) { "SecretAccessKey is empty" }

            appId = APP_ID
            appKey = APP_KEY
            accessKey = ACCESS_KEY_ID
            secretAccessKey = SECRET_ACCESS_KEY
        } else {
            appId = null
            appKey = null
            accessKey = null
            secretAccessKey = null
        }
    }

    companion object {
        private const val TAG = "RTCAppInfoRequest"
    }
}