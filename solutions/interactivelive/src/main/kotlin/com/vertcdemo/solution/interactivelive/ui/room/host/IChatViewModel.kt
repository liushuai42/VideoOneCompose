package com.vertcdemo.solution.interactivelive.ui.room.host

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

const val CONTENT_ID_HOST = "[Host]"

interface IChatViewModel {
    val chatList: StateFlow<List<AnnotatedString>>

    fun onMessage(userName: String, message: String, isHost: Boolean = false)
}

class ChatViewModel : IChatViewModel {
    private val _chatList = MutableStateFlow<List<AnnotatedString>>(emptyList())
    override val chatList = _chatList.asStateFlow()

    override fun onMessage(userName: String, message: String, isHost: Boolean) {
        _chatList.value += buildAnnotatedString {
            if (isHost) {
                appendInlineContent(id = CONTENT_ID_HOST)
                append(" ")
            }

            withStyle(style = userNameStyle) {
                append(userName)
            }
            append(": ")
            append(message)
        }
    }

    private val userNameStyle = SpanStyle(color = Color(0xBFFFFFFF))
}