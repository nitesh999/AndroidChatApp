package com.example.chatapplication.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.chatapplication.ChatApplication
import com.example.chatapplication.data.NotificationData
import com.example.chatapplication.data.PushNotification
import com.example.chatapplication.db.AppDatabase
import com.example.chatapplication.db.OfflineSavedChatMessage
import com.example.chatapplication.db.getInstance
import com.example.chatapplication.di.component.AppComponent
import com.example.chatapplication.di.helper.PreferencesHelper
import com.example.chatapplication.di.helper.WorkMangerHelper
import com.example.chatapplication.util.Constants
import com.example.chatapplication.util.NetworkInfo
import kotlinx.coroutines.*
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named


class ChatMessageViewModel(
    private val chatApplication: Context, private val preferencesHelper: PreferencesHelper,
    private val workManager: WorkManager, val offlineChatWork: OneTimeWorkRequest) : ViewModel() {

    var eventOfflineNetworkListener = MutableLiveData<Boolean>(false)
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun sendChatMessage(message:String, toUserId: String) {
        viewModelScope.launch {
            if (NetworkInfo.isNetworkAvailable(chatApplication)) {
                //val recipientUserId = "5e6610f8d4a93f48e459af05"
                if (message.isNotEmpty() && toUserId.isNotEmpty()) {
                    PushNotification(NotificationData(message)).also {
                        sendNotification(toUserId, it)
                    }
                }
            } else {
                mangeOfflineChatMessages(toUserId, message)
            }
        }
    }

    private fun sendNotification(toUserId: String, notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userToken = preferencesHelper.getString("userToken", "")
                val bearerToken = "Bearer ${userToken}"
                val response = ChatApplication.appComponent.getNetworkHelper().notificationAPI
                    .triggerNodeNotification(bearerToken, toUserId, notification)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val jsonObj = JSONObject(response.body()?.charStream()?.readText())
                        Toast.makeText(chatApplication, jsonObj.getString("message"), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(chatApplication, response.errorBody().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(chatApplication, e.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private suspend fun mangeOfflineChatMessages(toUserId: String, message: String) = withContext(Dispatchers.Main) {
        val userId = preferencesHelper.getString("userId", "")
        if (userId != null) {
            val chatMessage = OfflineSavedChatMessage(message, userId, toUserId)
            saveMessageLocally(toUserId, chatMessage)
            if(!WorkMangerHelper.getWorkStatus(chatApplication))
                workManager.enqueueUniqueWork(Constants.IS_OFFLINE_PENDING_MESSAGE, ExistingWorkPolicy.REPLACE, offlineChatWork)
            }
    }

    private fun saveMessageLocally(toUserId: String, offlineSavedChatMessage: OfflineSavedChatMessage) =
        CoroutineScope(Dispatchers.IO).launch {
            val database: AppDatabase? = getInstance(chatApplication)
            database?.messageDao()?.insertMessage(offlineSavedChatMessage)
            preferencesHelper.putStringSet("toUserIdMessagePendingList", toUserId)
            withContext(Dispatchers.Main) {
                eventOfflineNetworkListener.value = true
                Toast.makeText(chatApplication, "Please Switch On Internet", Toast.LENGTH_LONG).show()
            }
    }

    class Factory() : ViewModelProvider.Factory {
        @Inject @Named("appcontext")
        lateinit var chatApplication: Context
        @Inject
        lateinit var prefHelper: PreferencesHelper
        @Inject
        lateinit var oneTimeWorkRequest: OneTimeWorkRequest
        val workManager = WorkManager.getInstance(chatApplication)

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val appComponent: AppComponent = ChatApplication.appComponent
            if (modelClass.isAssignableFrom(ChatMessageViewModel::class.java)) {
                appComponent.inject(this)
                return ChatMessageViewModel(chatApplication, prefHelper, workManager, oneTimeWorkRequest) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}