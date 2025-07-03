package com.vertcdemo.solution.interactivelive.ui.room.host

import com.vertcdemo.solution.interactivelive.network.data.LiveSummary

sealed interface LiveFinishReason {
    data class End(val summary: LiveSummary) : LiveFinishReason
    object Timeout : LiveFinishReason
    object Irregularity : LiveFinishReason
    object Failed : LiveFinishReason
}
