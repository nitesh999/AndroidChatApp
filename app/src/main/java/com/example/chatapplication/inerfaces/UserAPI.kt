package com.example.chatapplication.inerfaces

import com.example.chatapplication.network.NetworkContainerUserList
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserAPI {

    @GET("users")
    fun getUsers(): Deferred<NetworkContainerUserList>

    @Multipart
    @PUT("user/{userId}")
    suspend fun updateUserProfile(
        @Header("Authorization") bearerToken: String,
        @Path("userId") userId: String?,
        @PartMap params:Map<String, @JvmSuppressWildcards RequestBody>
    ): Response<ResponseBody>
}