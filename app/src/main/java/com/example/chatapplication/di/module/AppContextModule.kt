package com.example.chatapplication.di.module

import android.content.Context
import com.example.chatapplication.ChatApplication
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class AppContextModule (val chatApplication: ChatApplication) {

    @Provides
    @Named("appcontext")
    fun provideContext() : Context {
        return chatApplication
    }
}