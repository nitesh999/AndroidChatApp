package com.example.chatapplication.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "message")
data class OfflineSavedChatMessage(var message: String, var fromId: String="", var toId: String="") {

    @PrimaryKey(autoGenerate = true)
    var messageId: Int = 0 // or foodId: Int? = null
    /*@PrimaryKey(autoGenerate = true)
    private val messageId = 0
    private val message: String? = null
    private val fromId = 0
    private val toId = 0*/
    /*@Ignore
    constructor(message: Int, fromId: Int, toId: Int) : this() {

    }*/
}