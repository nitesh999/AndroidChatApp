package com.example.chatapplication.di.module

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import com.example.chatapplication.util.Constants
import com.example.chatapplication.works.ChatMessageWork
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class WorkMangerModule {

    @Provides
    @Singleton
    fun providesOfflineWork(): OneTimeWorkRequest {
        return OneTimeWorkRequest.Builder(ChatMessageWork::class.java)
            .setConstraints(constraints)
            .addTag(Constants.IS_OFFLINE_PENDING_MESSAGE)
            .build()
    }

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
}