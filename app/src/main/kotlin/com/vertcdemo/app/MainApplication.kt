package com.vertcdemo.app

import android.app.Application
import com.vertcdemo.base.utils.ApplicationProvider

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationProvider.set(this)
    }
}