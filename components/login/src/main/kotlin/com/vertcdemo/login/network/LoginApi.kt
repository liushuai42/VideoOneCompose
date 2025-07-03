package com.vertcdemo.login.network

import com.vertcdemo.base.network.createEventService
import com.vertcdemo.base.network.data.EventBody
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("login")
    suspend fun login(@Body request: EventBody): LoginInfo?
}


val loginApiService by lazy {
    createEventService(LoginApi::class.java)
}
