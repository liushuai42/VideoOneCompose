package com.vertcdemo.solution.interactivelive.utils

import android.widget.Toast
import com.vertcdemo.base.utils.ApplicationProvider
import com.vertcdemo.solution.interactivelive.R

fun UNDER_CONSTRUCTION() {
    Toast.makeText(
        ApplicationProvider.get(),
        R.string.under_construction,
        Toast.LENGTH_SHORT
    ).show()
}