package com.example.chatapplication.viewmodels

import androidx.lifecycle.*
import com.example.chatapplication.ChatApplication
import com.example.chatapplication.db.User
import com.example.chatapplication.di.component.AppComponent
import com.example.chatapplication.di.helper.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.mladenrakonjac.modernandroidapp.data.UserRepository
import java.io.IOException
import javax.inject.Inject

class UserListViewModel(prefHelper: PreferencesHelper, userRepository: UserRepository) : ViewModel() {

    private val mPrefHelper = prefHelper
    private val mUserRepository = userRepository
    var userList = getFilteredListById(userRepository.users)

    private val query = MutableLiveData<String>()
    var userListUI: LiveData<List<User>> = Transformations.switchMap(query) { searchText ->
        var tempUserList: List<User>? = userList.value
        if (!searchText.isNullOrBlank()) {
            if (searchText.contains("@", true)) {
                tempUserList = tempUserList?.filter { it.email.contains(searchText) }
            } else {
                tempUserList = tempUserList?.filter { it.name.contains(searchText) }
            }
        }
        val tempMutLivedata = MutableLiveData<List<User>>()
        tempMutLivedata.value = tempUserList
        return@switchMap tempMutLivedata
    }


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
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData<Boolean>(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        refreshDataFromRepository()
    }

    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */
    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                mUserRepository.getUserList()
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if (userList.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

    private fun getFilteredListById(userList: LiveData<List<User>>): LiveData<List<User>> {
        val userId = mPrefHelper.getString("userId", "")
        return Transformations.map(userList) {
            it.filterNot { it._id == userId }
        }
    }

    fun getFilteredListByEmailOrName(searchText: String) {
        query.value = searchText
    }

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    class Factory() : ViewModelProvider.Factory {
        @Inject
        lateinit var userRepository: UserRepository

        @Inject
        lateinit var prefHelper: PreferencesHelper

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserListViewModel::class.java)) {
                val appComponent: AppComponent = ChatApplication.appComponent
                appComponent.inject(this)
                @Suppress("UNCHECKED_CAST")
                return UserListViewModel(prefHelper, userRepository) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}