package com.vertcdemo.base.utils

import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}