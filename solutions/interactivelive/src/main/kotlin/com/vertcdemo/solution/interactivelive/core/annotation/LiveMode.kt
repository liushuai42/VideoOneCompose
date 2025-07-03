package com.vertcdemo.solution.interactivelive.core.annotation

import androidx.annotation.IntDef

@IntDef(
    value = [
        LiveMode.NORMAL,
        LiveMode.LINK_PK,
        LiveMode.LINK_1v1,
        LiveMode.LINK_1vN
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class LiveMode {
    companion object {
        const val NORMAL: Int = 1
        const val LINK_PK: Int = 2
        const val LINK_1v1: Int = 3
        const val LINK_1vN: Int = 4
    }
}
