package com.vertcdemo.login.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vertcdemo.base.CredentialViewModel
import com.vertcdemo.base.KEY_CREDENTIAL
import com.vertcdemo.base.network.data.EventBody
import com.vertcdemo.base.utils.json
import com.vertcdemo.login.network.loginApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val credential: CredentialViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState.INPUT)

    val uiState = _uiState.asStateFlow()

    fun login(userName: String) {
        _uiState.value = LoginUiState.LOADING

        viewModelScope.launch {
            try {
                val loginInfo = withContext(Dispatchers.IO) {
                    loginApiService.login(
                        userName.loginBody()
                    )
                } ?: run {
                    _uiState.value = LoginUiState.INPUT
                    Log.d(TAG, "login: failed, info = null")
                    return@launch
                }
                credential.login(
                    userName = loginInfo.userName,
                    userId = loginInfo.userId,
                    token = loginInfo.loginToken,
                )
                Log.d(TAG, "login: success")
                _uiState.value = LoginUiState.SUCCESS
            } catch (e: Exception) {
                Log.e(TAG, "login: failed", e)
                _uiState.value = LoginUiState.INPUT
            }
        }
    }

    private fun String.loginBody() = EventBody(
        eventName = "passwordFreeLogin",
        content = json.encodeToString(mapOf("user_name" to this))
    )

    companion object {
        private const val TAG = "LoginViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val credential = this[KEY_CREDENTIAL]!!
                LoginViewModel(credential = credential)
            }
        }
    }
}

