package com.example.chatapplication.di.helper

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.chatapplication.util.Constants
import com.google.common.util.concurrent.ListenableFuture
import javax.inject.Named


class WorkMangerHelper {

    companion object {
        fun getWorkStatus(@Named("appcontext") context: Context): Boolean {
            val wm = WorkManager.getInstance(context)
            val future: ListenableFuture<List<WorkInfo>> = wm.getWorkInfosByTag(Constants.IS_OFFLINE_PENDING_MESSAGE)
            val list: List<WorkInfo> = future.get()
            for (workStatus in list) {
                if (workStatus.getState() === WorkInfo.State.RUNNING
                    || workStatus.getState() === WorkInfo.State.ENQUEUED) {
                    return true
                }
            }
            return false
        }
    }
}