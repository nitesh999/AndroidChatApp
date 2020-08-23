package com.example.chatapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chatapplication.db.dao.ChatMessageDao
import com.example.chatapplication.db.dao.UserDao
import javax.inject.Named

@Database(entities = arrayOf(OfflineSavedChatMessage::class, User::class), version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): ChatMessageDao
    abstract fun userDao(): UserDao
}

private lateinit var INSTANCE: AppDatabase
val DATABASE_NAME = "message_db"

@JvmField
val MIGRATION_4_5 = Migration4To5()

fun getInstance(@Named("appcontext") context: Context): AppDatabase {
    synchronized(AppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_4_5)
                .build()
        }
    }
    return INSTANCE
}
