package com.vertcdemo.base.network

import com.vertcdemo.base.network.data.EventReturn
import com.vertcdemo.base.utils.SolutionDataManager
import com.vertcdemo.base.utils.json
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

object EventReturnInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        response.body.use { responseBody ->
            if (!response.isSuccessful) {
                responseBody.close()
                throw HttpException.unknown("http code = " + response.code)
            } else {
                val eventReturn: EventReturn = json.decodeFromString(responseBody.string())

                if (eventReturn.code == 200) {
                    val newBody = eventReturn.string()?.toResponseBody(APPLICATION_JSON) ?: run {
                        throw HttpException.unknown("ResponseBody is null")
                    }
                    return response.newBuilder()
                        .body(newBody)
                        .build()
                } else {
                    if (eventReturn.code == HttpException.ERROR_CODE_TOKEN_EXPIRED
                        || eventReturn.code == HttpException.ERROR_CODE_TOKEN_EMPTY
                    ) {
                        SolutionDataManager.token = ""
                        // SolutionEventBus.post(AppTokenExpiredEvent())
                    }
                    throw HttpException(eventReturn.code, eventReturn.message)
                }
            }
        }
    }
}