package com.example.chatapplication.viewmodels

import androidx.databinding.ObservableField
import com.example.chatapplication.ChatApplication
import com.example.chatapplication.di.helper.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File


class UpdateUserProfileViewModel(private val preferencesHelper: PreferencesHelper) : RegistrationViewModel(preferencesHelper) {

    var userId: ObservableField<String> = ObservableField("")
    var profilePic: ObservableField<String> = ObservableField("")
    var currentPhotoPath: String = ""

    init {
        name.set(preferencesHelper.getString("userName"))
        email.set(preferencesHelper.getString("userEmail"))
        userId.set(preferencesHelper.getString("userId"))
        profilePic.set(preferencesHelper.getString("profilePic"))
    }

    fun updateUserProfile() {
        super.viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    showProgress.postValue(true)
                    val bearerToken = "Bearer ${preferencesHelper.getString("userToken")}"
                    val namedBody: RequestBody = name.get().toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    val emaildBody: RequestBody = email.get().toString().toRequestBody("text/plain".toMediaTypeOrNull())

                    val mapRequestBody:  MutableMap<String, RequestBody> = mutableMapOf(
                        "name" to namedBody,
                        "email" to emaildBody
                    )

                    lateinit var file:File
                    if(currentPhotoPath.isNotEmpty()) {
                        file = File(currentPhotoPath)
                        val fBody: RequestBody =
                            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        mapRequestBody.put("profilePic\"; filename=${file.name}", fBody)
                    }

                    val response = ChatApplication.appComponent.getNetworkHelper().updateUserProfileAPI.updateUserProfile(
                        bearerToken, userId.get(),
                        mapRequestBody
                    )
                    if (!response.isSuccessful) { // not logged in
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        showStatus.postValue(jsonObj.getString("error"))
                    } else {
                        showStatus.postValue("User Profile Updated Successfully")
                        preferencesHelper.putString("userName", name.get())
                        preferencesHelper.putString("userEmail", email.get())
                        if(currentPhotoPath.isNotEmpty()) {
                            preferencesHelper.putString("profilePic", file.name)
                        }
                    }
                    showProgress.postValue(false)
                } catch (e: Exception) {
                    showStatus.postValue(e.message)
                    showProgress.postValue(false)
                }
            }
        }
    }
}
