package com.vertcdemo.base.utils

import android.app.Application

object ApplicationProvider {
    private lateinit var application: Application

    @JvmStatic
    fun set(application: Application) {
        this.application = application
    }

    @JvmStatic
    fun get(): Application = application

    val applicationContext: Application
        get() = application
}