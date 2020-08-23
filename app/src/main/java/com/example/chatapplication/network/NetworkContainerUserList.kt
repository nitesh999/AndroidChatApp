package com.example.chatapplication.network

import com.example.chatapplication.db.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class NetworkContainerUserList(val userList: List<NetworkUser>)

@JsonClass(generateAdapter = true)
data class NetworkUser(
    val _id: String,
    val name: String,
    val email: String,
    val role: String,
    @Json(name = "profilePic") val profilePic: String? = ""
)

/**
 * Convert Network results to database objects
 */
fun NetworkContainerUserList.asDomainModel(): List<NetworkUser> {
    return userList.map {
        NetworkUser(
            _id = it._id,
            name = it.name,
            email = it.email,
            role = it.role,
            profilePic = it.profilePic
        )
    }
}


/**
 * Convert Network results to database objects
 */
fun NetworkContainerUserList.asDatabaseModel(): List<User> {
    return userList.map {
        User(
            _id = it._id,
            name = it.name,
            email = it.email,
            role = it.role,
            profilePic = it.profilePic!!
        )
    }
}


