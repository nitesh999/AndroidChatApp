package com.example.chatapplication.data

data class PushNotification(
    val data: NotificationData
    //val to: String = ""//firebaseToken will be taken from mongo db from node side using userid
)