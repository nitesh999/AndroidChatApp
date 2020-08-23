package com.example.chatapplication.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration4To5 : Migration(4,5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val TABLE_NAME_TEMP = "user"
        //val TABLE_NAME = "message"

        // 1. Create new table
        database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_NAME_TEMP` " +
                "(`id` TEXT NOT NULL, `name` TEXT NOT NULL, `email` TEXT NOT NULL, `role` TEXT NOT NULL, `db_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")

        // Rename Table
        //database.execSQL("ALTER TABLE $TABLE_NAME_TEMP RENAME TO users")

        // Add a new column
        database.execSQL("ALTER TABLE user ADD COLUMN profilePic TEXT DEFAULT \"\" NOT NULL");

        // Copy the com.example.chatapplication.data
        /*database.execSQL("INSERT INTO $TABLE_NAME_TEMP (game_name) "
                + "SELECT game_name "
                + "FROM $TABLE_NAME")

        // Remove the old table
        database.execSQL("DROP TABLE $TABLE_NAME")

        // Change the table name to the correct one
        database.execSQL("ALTER TABLE $TABLE_NAME_TEMP RENAME TO $TABLE_NAME")*/
    }
}
