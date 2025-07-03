package com.vertcdemo.solution.interactivelive.core.annotation

import androidx.annotation.IntDef

@IntDef(
    value = [
        LiveFinishType.NORMAL,
        LiveFinishType.TIMEOUT,
        LiveFinishType.IRREGULARITY
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class LiveFinishType {
    companion object {
        const val NORMAL: Int = 1
        const val TIMEOUT: Int = 2
        const val IRREGULARITY: Int = 3
    }
}