package com.vertcdemo.solution.interactivelive.core.annotation

import androidx.annotation.IntDef

@IntDef(
    value = [
        MediaStatus.ON,
        MediaStatus.OFF,
        MediaStatus.KEEP
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class MediaStatus {
    companion object {
        const val KEEP: Int = -1
        const val ON: Int = 1
        const val OFF: Int = 0
    }
}
