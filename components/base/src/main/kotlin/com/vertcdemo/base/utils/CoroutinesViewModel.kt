package com.vertcdemo.base.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class CoroutineViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)

    var job: Job? = null
        set(value) {
            if (field == value) {
                return
            }
            field?.cancel()
            field = value
        }

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {
        if (isLoading) {
            return
        }
        isLoading = true

        job = viewModelScope.launch(
            context = context,
            start = start,
            block = block
        )
    }

    fun cancel() {
        job = null
        isLoading = false
    }
}