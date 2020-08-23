package com.example.chatapplication.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.chatapplication.db.AppDatabase
import com.example.chatapplication.db.OfflineSavedChatMessage
import com.example.chatapplication.db.getInstance

class MessagesViewModel(application: Context) :
    AndroidViewModel(application.applicationContext as Application) {

    val database: AppDatabase = getInstance(getApplication())

    fun getPendingMessagesByToUserId(toUserId: String?): LiveData<List<OfflineSavedChatMessage>> {
        var allMessageOfflineSaved: LiveData<List<OfflineSavedChatMessage>>
        allMessageOfflineSaved = database.messageDao().loadMessageByToUserId(toUserId)
        return allMessageOfflineSaved
    }

    fun getAllPendingMessages(): LiveData<List<OfflineSavedChatMessage>> {
        return database.messageDao().loadAllMessages()
    }

    fun deletePendingMessageById(id: Int) {
        try {
            database.messageDao().deleteMessage(id)
        } catch (e: Exception) {
            println(e)
        }

    }
}
