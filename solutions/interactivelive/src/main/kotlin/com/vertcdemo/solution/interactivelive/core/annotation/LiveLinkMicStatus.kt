package com.vertcdemo.solution.interactivelive.core.annotation

import androidx.annotation.IntDef

@IntDef(
    value = [
        LiveLinkMicStatus.Companion.OTHER,
        LiveLinkMicStatus.Companion.INVITING,
        LiveLinkMicStatus.Companion.APPLYING,
        LiveLinkMicStatus.Companion.AUDIENCE_INTERACTING,
        LiveLinkMicStatus.Companion.HOST_INTERACTING
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class LiveLinkMicStatus {
    companion object {
        const val OTHER: Int = 0
        const val INVITING: Int = 1
        const val APPLYING: Int = 2
        const val AUDIENCE_INTERACTING: Int = 3
        const val HOST_INTERACTING: Int = 4
    }
}