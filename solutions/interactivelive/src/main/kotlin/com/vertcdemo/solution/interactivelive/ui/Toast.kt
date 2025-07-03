package com.vertcdemo.solution.interactivelive.ui

import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import com.vertcdemo.base.utils.ApplicationProvider

@MainThread
fun toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(ApplicationProvider.get(), text, duration).show()
}

@MainThread
fun toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(ApplicationProvider.get(), resId, duration).show()
}