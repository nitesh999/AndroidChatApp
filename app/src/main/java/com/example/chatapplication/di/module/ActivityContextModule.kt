package com.example.chatapplication.di.module

import android.app.Activity
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ActivityContextModule (val activity: Activity) {

    @Provides
    @Named("activitycontext")
    fun provideActivityContext() : Activity {
        return activity
    }
}