package com.example.chatapplication.di.module

import com.example.chatapplication.db.AppDatabase
import dagger.Module
import dagger.Provides
import me.mladenrakonjac.modernandroidapp.data.UserRepository
import javax.inject.Singleton

@Module
class UserRepositoryModule {

    @Provides
    @Singleton
    fun providesUserViewModel(appDatabase: AppDatabase): UserRepository {
        return UserRepository(appDatabase)
    }
}