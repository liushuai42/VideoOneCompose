package com.vertcdemo.base

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun StatusBarColor(
    darkIcons: Boolean = false
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowInsetsControllerCompat(window, view)
            controller.isAppearanceLightStatusBars = darkIcons
        }
    }
}