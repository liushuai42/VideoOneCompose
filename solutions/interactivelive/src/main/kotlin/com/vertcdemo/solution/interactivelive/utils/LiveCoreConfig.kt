package com.vertcdemo.solution.interactivelive.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.vertcdemo.base.utils.ApplicationProvider

object LiveCoreConfig {
    private const val PREFS_NAME = "live_streaming_config"

    private const val KEY_RTM_PULL_STREAMING = "rtm_pull_streaming"
    private const val KEY_ABR = "abr"

    private val prefs: SharedPreferences
        get() {
            val context = ApplicationProvider.get()
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

    var rtmPullStreaming: Boolean
        get() = prefs.getBoolean(KEY_RTM_PULL_STREAMING, false)
        set(value) {
            prefs.edit {
                putBoolean(KEY_RTM_PULL_STREAMING, value)
            }
        }

    var abr: Boolean
        get() = prefs.getBoolean(KEY_ABR, false)
        set(value) {
            prefs.edit {
                putBoolean(KEY_ABR, value)
            }
        }
}
