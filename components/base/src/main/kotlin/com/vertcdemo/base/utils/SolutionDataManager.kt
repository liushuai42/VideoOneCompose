package com.vertcdemo.base.utils

import android.content.Context
import android.content.SharedPreferences
import com.vertcdemo.base.utils.ApplicationProvider.applicationContext
import java.util.UUID
import kotlin.math.abs
import androidx.core.content.edit

object SolutionDataManager {
    private const val PREFS_NAME = "solution_data_manager"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_NAME = "user_name"
    const val KEY_TOKEN = "token"
    private const val KEY_DEVICE_ID = "device_id"
    private const val KEY_OPEN_UDID = "openudid"

    var userId: String
        get() = prefs.getString(KEY_USER_ID, "") ?: ""
        set(userId) = prefs.edit {
            putString(KEY_USER_ID, userId)
        }
    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(userName) = prefs.edit {
            putString(KEY_USER_NAME, userName)
        }
    var token: String
        get() = prefs.getString(KEY_TOKEN, "") ?: ""
        set(token) = prefs.edit {
            putString(KEY_TOKEN, token)
        }

    @get:Synchronized
    val deviceId: String
        get() {
            val did = prefs.getString(KEY_DEVICE_ID, "")
            return if (did.isNullOrEmpty()) {
                "${abs(UUID.randomUUID().hashCode())}".also {
                    prefs.edit { putString(KEY_DEVICE_ID, it) }
                }
            } else {
                did
            }
        }

    fun logout() = prefs.edit {
        remove(KEY_USER_ID)
        remove(KEY_USER_NAME)
        remove(KEY_TOKEN)
    }

    @JvmStatic
    fun ins(): SolutionDataManager = this

    private val prefs: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}