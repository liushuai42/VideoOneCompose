package com.vertcdemo.solution.interactivelive.core.annotation

import androidx.annotation.IntDef

@IntDef(
    value = [
        GiftType.LIKE,
        GiftType.SUGAR,
        GiftType.DIAMOND,
        GiftType.FIREWORKS
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class GiftType {
    companion object {
        const val LIKE: Int = 1
        const val SUGAR: Int = 2
        const val DIAMOND: Int = 3
        const val FIREWORKS: Int = 4
    }
}
