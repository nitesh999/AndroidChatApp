package com.example.chatapplication.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.example.chatapplication.ChatApplication
import com.example.chatapplication.data.NotificationData
import com.example.chatapplication.data.PushNotification
import com.example.chatapplication.db.AppDatabase
import com.example.chatapplication.db.OfflineSavedChatMessage
import com.example.chatapplication.db.getInstance
import com.example.chatapplication.util.Constants
import com.example.chatapplication.util.NetworkInfo
import com.example.chatapplication.works.ChatMessageWork
import kotlinx.coroutines.*
import org.json.JSONObject


class ChatMessageViewModel(application: Application) : AndroidViewModel(application) {

    //lateinit var messagesList: MutableLiveData<NotificationData>
    val mSharedPreference: SharedPreferences = application.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val workManager = WorkManager.getInstance(application)
    lateinit var offlineChatWork: OneTimeWorkRequest
    var eventOfflineNetworkListener = MutableLiveData<Boolean>(false)
    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    fun sendChatMessage(message:String, toUserId: String) {
        viewModelScope.launch {
            if (NetworkInfo.isNetworkAvailable(getApplication())) {
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

    fun startWorkManager(toUserId: String) {
        //val getAllMessagesViewModel = GetAllMessagesViewModel(getApplication())
        //val pendingMessages = getAllMessagesViewModel.getPendingMessages()
        val chatData: Data = workDataOf("toUserId" to toUserId)
        offlineChatWork = OneTimeWorkRequest.Builder(ChatMessageWork::class.java)
            .setInputData(chatData)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(offlineChatWork)
    }

    // Create charging constraint
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun saveMessageLocallyAndInitWorkmanager(toUserId: String, offlineSavedChatMessage: OfflineSavedChatMessage) =
        CoroutineScope(Dispatchers.IO).launch {
            val database: AppDatabase? = getInstance(getApplication())
            database?.messageDao()?.insertMessage(offlineSavedChatMessage)
            withContext(Dispatchers.Main) {
                startWorkManager(toUserId)
                eventOfflineNetworkListener.value = true
                mSharedPreference.edit().putBoolean(Constants.IS_OFFLINE_PENDING_MESSAGE, true)
                Toast.makeText(getApplication(), "Please Switch On Internet", Toast.LENGTH_LONG).show()
            }

    }

    private suspend fun mangeOfflineChatMessages(toUserId: String, message: String) = withContext(Dispatchers.Main){
        val userId = mSharedPreference.getString("userId", "")
        if (userId != null && toUserId != null) {
            val chatMessage = OfflineSavedChatMessage(message, userId, toUserId)
            saveMessageLocallyAndInitWorkmanager(toUserId, chatMessage)
        }
    }

    fun sendNotification(toUserId: String, notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userToken = mSharedPreference.getString("userToken", "")
                val bearerToken = "Bearer ${userToken}"
                val response = ChatApplication.appComponent.getNetworkHelper().notificationAPI.triggerNodeNotification(bearerToken, toUserId, notification)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val jsonObj = JSONObject(response.body()?.charStream()?.readText())
                        Toast.makeText(getApplication(), jsonObj.getString("message"), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(getApplication(), response.errorBody().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
}