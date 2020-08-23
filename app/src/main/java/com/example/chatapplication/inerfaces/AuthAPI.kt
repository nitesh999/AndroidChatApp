package com.example.chatapplication.inerfaces

import com.example.chatapplication.network.NetworkContainerUserLogin
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthAPI {

    //@Headers("Content-Type:$CONTENT_TYPE")
    @POST("signup")
    fun register(@Body body: Map<String, String>): Deferred<NetworkContainerUserLogin>

    @POST("signin")
    fun signin(@Body body: Map<String, String>): Deferred<NetworkContainerUserLogin>
}