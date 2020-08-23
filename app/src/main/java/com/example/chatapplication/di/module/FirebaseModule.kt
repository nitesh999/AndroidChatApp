package com.example.chatapplication.di.module

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
class FirebaseModule {

    @Provides
    fun getFirebase():FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
}