package com.example.chatapplication.broadcasts;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.chatapplication.util.Constants
import com.example.chatapplication.works.ChatMessageWork

class WorkManagerStartReceiver : BroadcastReceiver() {

    lateinit var workManager: WorkManager
    override fun onReceive(context: Context, intent: Intent) {
        println("WorkManagerStartReceiver")
        val mSharedPreference: SharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        if (mSharedPreference.getBoolean(Constants.IS_OFFLINE_PENDING_MESSAGE, false)) {
            workManager = WorkManager.getInstance(context)
            startWorkManager()
        }
    }

    private fun startWorkManager() {
        val offlineChatWork: OneTimeWorkRequest =
            OneTimeWorkRequest.Builder(ChatMessageWork::class.java)
                //.setInputData(chatData)
                .setConstraints(constraints)
                .build()
        workManager.enqueue(offlineChatWork)
    }

    // Create charging constraint
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
}
