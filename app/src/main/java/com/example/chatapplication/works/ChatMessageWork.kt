package com.example.chatapplication.works

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.chatapplication.ChatApplication
import com.example.chatapplication.data.NotificationData
import com.example.chatapplication.data.PushNotification
import com.example.chatapplication.db.OfflineSavedChatMessage
import com.example.chatapplication.util.Constants
import com.example.chatapplication.viewmodels.MessagesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatMessageWork(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    lateinit var outputData: Data
    private var isDeliveryFailure: Boolean = true
    private val mSharedPreference: SharedPreferences = ctx.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)

    override fun doWork(): Result {
        try {
            println("doWork")
            val toIdPendingList: MutableSet<String>? =
                mSharedPreference.getStringSet("toUserIdMessagePendingList", HashSet<String>())
            val messagesViewModel = MessagesViewModel(applicationContext)
            lateinit var liveDataMessages: List<OfflineSavedChatMessage>
            if (toIdPendingList != null) {
                for (toId in toIdPendingList) {
                    if (!toId.isEmpty()) {//came from a specific chat screen so deliver only to user chat messages
                        liveDataMessages = messagesViewModel.getPendingMessagesByToUserId(toId)
                    } else {//came from broadcast receiver so deliver all messages
                        liveDataMessages = messagesViewModel.getAllPendingMessages()
                    }
                }
            }

            if (liveDataMessages != null) {
                for (message in liveDataMessages) {
                    PushNotification(NotificationData(message.message)).let {
                        CoroutineScope(Dispatchers.IO).launch {
                            val userToken = mSharedPreference.getString("userToken", "")
                            val bearerToken = "Bearer $userToken"
                            val response = ChatApplication.appComponent.getNetworkHelper().notificationAPI
                                    .triggerNodeNotification(bearerToken, message.toId, it)
                            if (response.isSuccessful) {
                                messagesViewModel.deletePendingMessageById(message.messageId)
                                val jsonObj = JSONObject(response.body()?.charStream()?.readText())
                                outputData = workDataOf(Constants.KEY_RESULT to jsonObj.getString("message"))
                                isDeliveryFailure = false
                            } else {
                                isDeliveryFailure = true
                                //Result.failure()
                            }
                        }
                    }
                }
                if (isDeliveryFailure) {
                    return Result.failure()
                } else {
                    return Result.success(outputData)
                }
            }
        } catch (exception: Exception) {
            Result.failure()
        }
        return Result.failure()
    }
}
