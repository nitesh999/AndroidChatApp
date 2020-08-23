package com.example.chatapplication.di.component

import com.example.chatapplication.ChatApplication
import com.example.chatapplication.di.helper.PreferencesHelper
import com.example.chatapplication.di.module.AppContextModule
import com.example.chatapplication.di.module.DatabaseModule
import com.example.chatapplication.di.module.UserRepositoryModule
import com.example.chatapplication.network.NetworkHelper
import com.example.chatapplication.viewmodels.LoginViewModel
import com.example.chatapplication.viewmodels.RegistrationViewModel
import com.example.chatapplication.viewmodels.UpdateUserProfileViewModel
import com.example.chatapplication.viewmodels.UserListViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppContextModule::class, DatabaseModule::class, UserRepositoryModule::class])
interface AppComponent {

    fun getPrefHelper(): PreferencesHelper
    fun getNetworkHelper(): NetworkHelper

    fun inject(application: ChatApplication)
    fun inject(userListViewModelFactory: UserListViewModel.Factory)
    fun inject(loginViewModelFactory: LoginViewModel.Factory)
    fun inject(RegistrationViewModel: RegistrationViewModel)
    fun inject(updateUserProfileViewModel: UpdateUserProfileViewModel)
}
