package com.vertcdemo.solution.interactivelive.core.annotation

import androidx.annotation.IntDef

@IntDef(
    value = [
        LivePermitType.ACCEPT,
        LivePermitType.REJECT
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class LivePermitType {
    companion object {
        const val ACCEPT: Int = 1
        const val REJECT: Int = 2
    }
}
