package com.example.chatapplication.broadcasts;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.chatapplication.di.helper.WorkMangerHelper
import javax.inject.Inject

class WorkManagerStartReceiver : BroadcastReceiver() {

    @Inject
    lateinit var offlineChatWork: OneTimeWorkRequest
    private lateinit var workManager: WorkManager

    override fun onReceive(context: Context, intent: Intent) {
        println("WorkManagerStartReceiver")
        startWorkManager(context)
    }

    private fun startWorkManager(context: Context) {
        workManager = WorkManager.getInstance(context)
        if(!WorkMangerHelper.getWorkStatus(context))
            workManager.enqueue(offlineChatWork)
    }
}
