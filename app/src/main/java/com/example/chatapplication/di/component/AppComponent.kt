package com.example.chatapplication.di.component

import androidx.work.OneTimeWorkRequest
import com.example.chatapplication.ChatApplication
import com.example.chatapplication.di.helper.PreferencesHelper
import com.example.chatapplication.di.module.AppContextModule
import com.example.chatapplication.di.module.DatabaseModule
import com.example.chatapplication.di.module.UserRepositoryModule
import com.example.chatapplication.di.module.WorkMangerModule
import com.example.chatapplication.network.NetworkHelper
import com.example.chatapplication.viewmodels.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppContextModule::class, DatabaseModule::class, UserRepositoryModule::class, WorkMangerModule::class])
interface AppComponent {

    fun getPrefHelper(): PreferencesHelper
    fun getNetworkHelper(): NetworkHelper
    fun getOneTimeWorkRequest(): OneTimeWorkRequest

    fun inject(application: ChatApplication)
    fun inject(userListViewModelFactory: UserListViewModel.Factory)
    fun inject(loginViewModelFactory: LoginViewModel.Factory)
    fun inject(RegistrationViewModel: RegistrationViewModel)
    fun inject(updateUserProfileViewModel: UpdateUserProfileViewModel)
    fun inject(chatMessageViewModel: ChatMessageViewModel.Factory)
}
