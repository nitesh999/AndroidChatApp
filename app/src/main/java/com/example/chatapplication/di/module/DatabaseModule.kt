package com.example.chatapplication.di.module

import android.content.Context
import com.example.chatapplication.db.AppDatabase
import com.example.chatapplication.db.getInstance
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun providesDbHelper(@Named("appcontext") context: Context): AppDatabase {
        return getInstance(context)
    }
}