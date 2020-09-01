package com.example.chatapplication.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.chatapplication.db.OfflineSavedChatMessage

@Dao
interface ChatMessageDao {

    @Insert
    fun insertMessage(messageEntryOfflineSaved: OfflineSavedChatMessage): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateMessage(messageEntryOfflineSaved: OfflineSavedChatMessage): Int

    @Query("DELETE FROM message WHERE messageId = :id")
    fun deleteMessage(id: Int)

    @Query("SELECT * FROM message")
    fun loadAllMessages(): LiveData<List<OfflineSavedChatMessage>>

    @Query("SELECT * FROM message")
    fun loadAllMessagesList(): List<OfflineSavedChatMessage>

    @Query("SELECT * FROM message WHERE messageId = :id")
    fun loadMessageById(id: String?): LiveData<List<OfflineSavedChatMessage>>

    @Query("SELECT * FROM message WHERE toId = :id")
    fun loadMessageByToUserId(id: String?): LiveData<List<OfflineSavedChatMessage>>

    @Query("SELECT * FROM message WHERE toId = :id")
    fun loadMessageByToUserIdList(id: String?): List<OfflineSavedChatMessage>

    @Query("DELETE FROM message")
    fun deleteAllMessages()
}