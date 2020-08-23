package com.example.chatapplication.di.component;

import com.example.chatapplication.activity.MainActivity
import com.example.chatapplication.di.module.ActivityContextModule
import com.example.chatapplication.di.module.FirebaseModule
import com.example.chatapplication.di.scope.ActivityScope
import dagger.Component

@ActivityScope
@Component(dependencies = [AppComponent::class], modules = [FirebaseModule::class, ActivityContextModule::class])
interface ActivityComponent {

    fun inject(mainActivity:MainActivity);

}