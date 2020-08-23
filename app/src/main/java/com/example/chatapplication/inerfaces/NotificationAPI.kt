package com.example.chatapplication.inerfaces

import com.example.chatapplication.data.PushNotification
import com.example.chatapplication.util.Constants.Companion.CONTENT_TYPE
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface NotificationAPI {

    @POST("notification/sendNotification/{toUserId}")
    suspend fun triggerNodeNotification(
        @Header("Authorization") bearerToken: String,
        @Path("toUserId") userId: String,
        @Body notification: PushNotification
    ): Response<ResponseBody>


    @Headers("Content-Type:$CONTENT_TYPE")
    @PUT("notification/firebaseToken/{toUserId}")
    suspend fun sendFirebaseToken(
        @Header("Authorization") bearerToken: String,
        @Path("toUserId") userId: String,
        @Body token: Map<String, String>
    ): Response<ResponseBody>
}