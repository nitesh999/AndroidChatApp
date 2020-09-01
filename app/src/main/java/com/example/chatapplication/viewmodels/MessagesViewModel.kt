package com.example.chatapplication.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.chatapplication.db.AppDatabase
import com.example.chatapplication.db.OfflineSavedChatMessage
import com.example.chatapplication.db.getInstance

class MessagesViewModel(application: Context) : AndroidViewModel(application.applicationContext as Application) {

    val database: AppDatabase = getInstance(getApplication())

    fun getPendingMessagesByToUserId(toUserId: String?): List<OfflineSavedChatMessage> {
        return database.messageDao().loadMessageByToUserIdList(toUserId)
    }

    fun getAllPendingMessages(): List<OfflineSavedChatMessage> {
        return database.messageDao().loadAllMessagesList()
    }

    fun deletePendingMessageById(id: Int) {
        try {
            database.messageDao().deleteMessage(id)
        } catch (e: Exception) {
            println(e)
        }
    }
}
