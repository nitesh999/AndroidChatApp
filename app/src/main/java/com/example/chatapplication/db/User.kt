package com.example.chatapplication.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
data class User (@SerializedName("_id") @PrimaryKey val _id: String,
                 @SerializedName("name") val name: String,
                 @SerializedName("email") val email: String,
                 @SerializedName("role") val role: String,
                 @SerializedName("role") val profilePic: String)

/**
 * Map Database to domain entities
 */
fun List<User>.asDatabaseModel(): List<User> {
    return map {
        User(
            _id = it._id,
            name = it.name,
            email = it.email,
            role = it.role,
            profilePic = it.profilePic)
    }
}
