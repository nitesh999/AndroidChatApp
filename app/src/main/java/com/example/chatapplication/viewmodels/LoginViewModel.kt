package com.example.chatapplication.viewmodels

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chatapplication.ChatApplication
import com.example.chatapplication.di.component.AppComponent
import com.example.chatapplication.di.helper.PreferencesHelper
import com.example.chatapplication.network.NetworkContainerUserLogin
import com.example.chatapplication.network.NetworkUser
import com.example.chatapplication.network.asDomainModel
import com.example.chatapplication.util.Util
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

open class LoginViewModel(private val preferencesHelper: PreferencesHelper) : ViewModel() {


    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var btnSelected: ObservableBoolean
    var email: ObservableField<String>
    var password: ObservableField<String>
    var showStatus: MutableLiveData<String>
    var userData: MutableLiveData<NetworkUser>
    var showProgress: MutableLiveData<Boolean>

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        btnSelected = ObservableBoolean(false)
        showStatus = MutableLiveData("")
        email = ObservableField("")
        password = ObservableField("")
        userData = MutableLiveData()
        showProgress = MutableLiveData(false)
    }

    fun onEmailChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        btnSelected.set(Util.isEmailValid(s.toString()) && password.get()!!.length >= 8)
    }

    fun onPasswordChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        btnSelected.set(Util.isEmailValid(email.get()!!) && s.toString().length >= 8)
    }

    fun loginUser() {
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    showProgress.postValue(true)
                    val map = mapOf("email" to email.get().toString(), "password" to password.get().toString())
                    val response = ChatApplication.appComponent.getNetworkHelper().authApi.signin(map).await()
                    registerFirebaseNotification(response)
                } catch (e: Exception) {
                    showStatus.postValue(e.message)
                    showProgress.postValue(false)
                }
            }
        }
    }

    fun registerFirebaseNotification(userLoginDetails: NetworkContainerUserLogin) {
        saveUserDetails(userLoginDetails)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val firebaseToken = it.token
            CoroutineScope(Dispatchers.IO).launch {
                val mapToken = mapOf("token" to firebaseToken)
                val bearerToken = "Bearer ${preferencesHelper.getString("userToken")}"
                val response = ChatApplication.appComponent.getNetworkHelper().notificationAPI.sendFirebaseToken(bearerToken, userLoginDetails.user._id, mapToken)
                if (!response.isSuccessful) { // not logged in
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    showStatus.postValue(jsonObj.getString("error"))
                } else {
                    showStatus.postValue("Login Successful")
                    userData.postValue(userLoginDetails.asDomainModel());
                }
                showProgress.postValue(false)
            }
        }
    }

    fun saveUserDetails(response: NetworkContainerUserLogin) {
        val user: NetworkUser = response.user;
        preferencesHelper.putString("userToken", response.token)
        preferencesHelper.putString("userId", user._id)
        preferencesHelper.putString("userName", user.name)
        preferencesHelper.putString("userEmail", user.email)
        preferencesHelper.putString("profilePic", user.profilePic)
    }

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory() : ViewModelProvider.Factory {
        @Inject
        lateinit var prefHelper: PreferencesHelper

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val appComponent: AppComponent = ChatApplication.appComponent
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                appComponent.inject(this)
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(prefHelper) as T
            }else if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
                appComponent.inject(this)
                return RegistrationViewModel(prefHelper) as T
            }else if (modelClass.isAssignableFrom(UpdateUserProfileViewModel::class.java)) {
                appComponent.inject(this)
                return UpdateUserProfileViewModel(prefHelper) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}