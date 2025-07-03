package com.vertcdemo.solution.interactivelive.core.annotation

import androidx.annotation.IntDef

@IntDef(
    value = [
        MessageType.MSG,
        MessageType.GIFT,
        MessageType.LIKE
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class MessageType {
    companion object {
        const val MSG = 1
        const val GIFT = 2
        const val LIKE = 3
    }
}