package com.vertcdemo.base

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import com.vertcdemo.base.utils.SolutionDataManager
import com.vertcdemo.base.utils.SolutionDataManager.KEY_USER_ID
import com.vertcdemo.base.utils.SolutionDataManager.KEY_USER_NAME
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

val KEY_CREDENTIAL = CreationExtras.Key<CredentialViewModel>()

class CredentialViewModel : ViewModel() {
    private val _userName = MutableStateFlow(SolutionDataManager.userName)

    private val _userId = MutableStateFlow(SolutionDataManager.userId)

    val userName = _userName.asStateFlow()
    val userId = _userId.asStateFlow()

    fun login(userId: String, userName: String, token: String) {
        SolutionDataManager.apply {
            this.userId = userId
            this.userName = userName
            this.token = token
        }
    }

    fun logout() = SolutionDataManager.logout()

    fun deleteAccount() = SolutionDataManager.logout()

    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                KEY_USER_NAME -> _userName.value = SolutionDataManager.userName
                KEY_USER_ID -> _userId.value = SolutionDataManager.userId
            }
        }

    init {
        SolutionDataManager.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onCleared() {
        SolutionDataManager.unregisterOnSharedPreferenceChangeListener(listener)
        super.onCleared()
    }
}