package com.vertcdemo.solution.interactivelive.core.annotation

import androidx.annotation.IntDef


@IntDef(
    value = [
        RoomStatus.LIVE,
        RoomStatus.AUDIENCE_LINK,
        RoomStatus.PK
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class RoomStatus {
    companion object {
        const val LIVE: Int = LiveLinkMicStatus.OTHER
        const val AUDIENCE_LINK: Int = LiveLinkMicStatus.AUDIENCE_INTERACTING
        const val PK: Int = LiveLinkMicStatus.HOST_INTERACTING
    }
}
