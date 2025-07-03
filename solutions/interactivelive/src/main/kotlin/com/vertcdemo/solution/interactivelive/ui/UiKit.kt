package com.vertcdemo.solution.interactivelive.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val ZeroPadding = PaddingValues(all = 0.dp)

fun Modifier.liveBackground() = this.background(
    brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF250214),
            Color(0xFF0D0B53),
        )
    )
)