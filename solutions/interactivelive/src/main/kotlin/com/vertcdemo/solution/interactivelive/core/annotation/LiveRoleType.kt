package com.vertcdemo.solution.interactivelive.core.annotation

import androidx.annotation.IntDef

@IntDef(
    value = [
        LiveRoleType.Companion.AUDIENCE,
        LiveRoleType.Companion.HOST
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class LiveRoleType {
    companion object {
        const val AUDIENCE: Int = 1
        const val HOST: Int = 2
    }
}
