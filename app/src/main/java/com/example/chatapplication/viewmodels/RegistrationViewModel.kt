package com.example.chatapplication.viewmodels

import androidx.databinding.ObservableField
import com.example.chatapplication.ChatApplication
import com.example.chatapplication.di.helper.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class RegistrationViewModel(preferencesHelper: PreferencesHelper) : LoginViewModel(preferencesHelper) {

    var name: ObservableField<String>

    init {
        name = ObservableField("")
    }

    fun onNameChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        btnSelected.set(name.get()!!.length >= 2)
    }

    fun registerUser() {
        super.viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    showProgress.postValue(true)
                    val map = mapOf(
                        "name" to name.get().toString(),
                        "email" to email.get().toString(),
                        "password" to password.get().toString()
                    )
                    val response = ChatApplication.appComponent.getNetworkHelper().authApi.register(map).await()
                    super.registerFirebaseNotification(response)
                } catch (e: Exception) {
                    showStatus.postValue(e.message)
                    showProgress.postValue(false)
                }
            }
        }
    }
}
