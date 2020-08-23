package com.example.chatapplication.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.chatapplication.db.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(user: List<User>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUser(user: User): Int

    @Query("DELETE FROM user WHERE _id = :id")
    fun deleteUser(id: Int): Int

    @Query("SELECT * FROM user")
    fun loadAllUser(): LiveData<List<User>>

    @Query("SELECT * FROM user WHERE _id = :id")
    fun loadUserById(id: Int): LiveData<User>

    @Query("DELETE FROM user")
    fun deleteAllUsers()
}
