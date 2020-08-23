package me.mladenrakonjac.modernandroidapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.chatapplication.ChatApplication
import com.example.chatapplication.db.AppDatabase
import com.example.chatapplication.db.User
import com.example.chatapplication.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for User Repository models
 */


class UserRepository(private val database: AppDatabase) {

    val users: LiveData<List<User>> = Transformations.map(database.userDao().loadAllUser()) { it }

    suspend fun getUserList() = withContext(Dispatchers.IO) {
        val response = ChatApplication.appComponent.getNetworkHelper().userAPI.getUsers().await()
        database.userDao().insertUsers(response.asDatabaseModel())
    }
}



