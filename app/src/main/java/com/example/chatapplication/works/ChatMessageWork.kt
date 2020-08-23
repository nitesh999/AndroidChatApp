package com.example.chatapplication.works

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.chatapplication.ChatApplication
import com.example.chatapplication.data.NotificationData
import com.example.chatapplication.data.PushNotification
import com.example.chatapplication.db.OfflineSavedChatMessage
import com.example.chatapplication.util.Constants
import com.example.chatapplication.util.Constants.Companion.IS_OFFLINE_PENDING_MESSAGE
import com.example.chatapplication.viewmodels.MessagesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ChatMessageWork(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    lateinit var outputData: Data
    var isDeliveryFailure: Boolean = true
    val mSharedPreference: SharedPreferences = ctx.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)

    override suspend fun doWork(): Result = withContext(Dispatchers.Main) {
        try {
            println("doWork")
            val toId: String? = inputData.getString("toUserId")
            val messagesViewModel = MessagesViewModel(applicationContext)
            //Handler(Looper.getMainLooper()).post {
            val liveDataMessages:LiveData<List<OfflineSavedChatMessage>>
            if (!toId.isNullOrEmpty()) {//came from a specific chat screen so deliveronly to user chat messages
                liveDataMessages = messagesViewModel.getPendingMessagesByToUserId(toId)
            } else {//came from broadcast receiver so deliver all messages
                liveDataMessages = messagesViewModel.getAllPendingMessages()
            }

            liveDataMessages.observeForever { pendingMessages -> // do something with stuff
                if (pendingMessages != null) {
                    for (message in pendingMessages) {
                        PushNotification(NotificationData(message.message)).let {
                            CoroutineScope(Dispatchers.IO).launch {
                                val userToken = mSharedPreference.getString("userToken", "")
                                val bearerToken = "Bearer ${userToken}"
                                val response = ChatApplication.appComponent.getNetworkHelper().notificationAPI.triggerNodeNotification(bearerToken, message.toId, it)
                                if (response.isSuccessful) {
                                    messagesViewModel.deletePendingMessageById(message.messageId)
                                    val jsonObj = JSONObject(response.body()?.charStream()?.readText())
                                    outputData = workDataOf(Constants.KEY_RESULT to jsonObj.getString("message"))
                                    Result.success(outputData)
                                } else {
                                    isDeliveryFailure = true
                                    //Result.failure()
                                }
                            }
                        }
                    }
                    if (isDeliveryFailure) {
                        Result.failure()
                    } else {
                        mSharedPreference.edit().putBoolean(IS_OFFLINE_PENDING_MESSAGE, false)
                        Result.success(outputData)
                    }
                }
            }
            //}
            //val usersFromApi = sendMessageFromApiDeferred.await()
            //Result.retry()
            if (isDeliveryFailure) {
                Result.failure()
            } else {
                Result.success(outputData)
            }
        } catch (exception: Exception) {
            Result.failure()
        }
    }

}
