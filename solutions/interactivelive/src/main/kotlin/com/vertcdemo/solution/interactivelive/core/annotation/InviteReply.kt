package com.vertcdemo.solution.interactivelive.core.annotation

import androidx.annotation.IntDef

@IntDef(
    value = [
        InviteReply.WAITING,
        InviteReply.ACCEPT,
        InviteReply.REJECT,
        InviteReply.TIMEOUT,
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class InviteReply(val value: Int) {
    companion object {
        const val WAITING = 0
        const val ACCEPT = 1
        const val REJECT = 2
        const val TIMEOUT = 3
    }
}