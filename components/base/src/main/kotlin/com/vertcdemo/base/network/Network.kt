package com.vertcdemo.base.network

import android.util.Log
import com.vertcdemo.base.BuildConfig
import com.vertcdemo.base.BuildConfig.SERVER_URL
import com.vertcdemo.base.network.data.EventReturn
import com.vertcdemo.base.utils.SolutionDataManager
import com.vertcdemo.base.utils.json
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.typeOf

private const val TAG = "VONetwork"

val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .debugLog()
        .build()
}

private val retrofitEvent: Retrofit by lazy {
    val client = okHttpClient.newBuilder()
        .addInterceptor(EventReturnInterceptor)
        .build()

    Retrofit.Builder()
        .client(client)
        .addConverterFactory(json.asConverterFactory(APPLICATION_JSON))
        .baseUrl("$SERVER_URL/")
        .build()
}

fun <T> createEventService(clazz: Class<T>): T {
    return retrofitEvent.create(clazz)
}

suspend inline fun <reified T, reified R> sendEvent(
    eventName: String,
    body: T
): R {
    val element = sendEvent(
        eventName = eventName,
        body = json.encodeToString(body)
    )

    if (R::class == Unit::class) {
        return Unit as R
    } else if (R::class == Any::class) {
        return element as R
    }

    if (element == null) {
        val type = typeOf<R>()
        if (type.isMarkedNullable) {
            return null as R
        }
        throw HttpException.unknown("EventReturn response is null")
    }

    return json.decodeFromJsonElement<R>(element)
}

suspend fun sendEvent(
    eventName: String,
    body: String
): JsonElement? = suspendCancellableCoroutine { cont ->
    val request = Request.Builder()
        .url(
            SERVER_URL.toHttpUrl()
                .newBuilder()
                .addPathSegment("http_call")
                .addQueryParameter("event_name", eventName)
                .build()
        )
        .header("X-Login-Token", SolutionDataManager.token)
        .post(body.toRequestBody(APPLICATION_JSON))
        .build()

    okHttpClient.newCall(request)
        .enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "onFailure: $e")
                cont.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw HttpException.unknown("http code = " + response.code)
                } else {
                    val responseBody = response.body
                    val eventReturn: EventReturn = json.decodeFromString(responseBody.string())

                    if (eventReturn.code == 200) {
                        cont.resume(eventReturn.response)
                    } else {
                        if (eventReturn.code == HttpException.ERROR_CODE_TOKEN_EXPIRED
                            || eventReturn.code == HttpException.ERROR_CODE_TOKEN_EMPTY
                        ) {
                            SolutionDataManager.logout()
                        }
                        cont.resumeWithException(
                            HttpException(eventReturn.code, eventReturn.message)
                        )
                    }
                }
            }
        })
}

private fun OkHttpClient.Builder.debugLog(): OkHttpClient.Builder {
    if (BuildConfig.DEBUG) {
        addInterceptor(HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    }
    return this
}

val APPLICATION_JSON: MediaType = "application/json; charset=utf-8".toMediaType()

