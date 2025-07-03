package com.vertcdemo.solution.interactivelive.ui.room.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface IDialogState {
    val state: StateFlow<Boolean>
    fun show()
    fun dismiss()

    @Composable
    fun collectAsState(context: CoroutineContext = EmptyCoroutineContext) =
        state.collectAsState(context)
}

class DialogState : IDialogState {
    private val _state = MutableStateFlow(false)
    override val state = _state.asStateFlow()

    override fun show() {
        _state.value = true
    }

    override fun dismiss() {
        _state.value = false
    }
}

class ArgumentDialogState {
    private val _state = MutableStateFlow<DialogArgs>(DialogArgs.Dismiss)
    val state = _state.asStateFlow()

    fun show(args: Any) {
        _state.value = DialogArgs.Show(args)
    }

    fun dismiss() {
        _state.value = DialogArgs.Dismiss
    }

    @Composable
    fun collectAsState(context: CoroutineContext = EmptyCoroutineContext) =
        state.collectAsState(context)
}

sealed interface DialogArgs {
    object Dismiss : DialogArgs
    data class Show(val args: Any) : DialogArgs
}

