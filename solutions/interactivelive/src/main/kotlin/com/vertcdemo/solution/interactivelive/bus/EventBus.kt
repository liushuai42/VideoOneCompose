package com.vertcdemo.solution.interactivelive.bus

import android.util.Log
import org.greenrobot.eventbus.EventBus


object LiveEventBus {
    private const val TAG = "LiveEventBus"
    private val eventBus = EventBus.builder()
        .build()

    fun post(event: Any) {
        if (event.javaClass.getAnnotation(SkipLogging::class.java) == null) {
            Log.d(TAG, "event=${event.javaClass}")
        }
        eventBus.post(event)
    }

    fun register(subscriber: Any) {
        eventBus.register(subscriber)
    }

    fun unregister(subscriber: Any) {
        eventBus.unregister(subscriber)
    }
}

/**
 * Mark the event not print by SolutionEventBus Log when post
 */
@Target(AnnotationTarget.CLASS)
annotation class SkipLogging