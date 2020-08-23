package com.example.chatapplication.network

import com.example.chatapplication.db.User
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class NetworkContainerUserLogin(val token: String?, val user: NetworkUser)

/*@JsonClass(generateAdapter = true)
data class NetworkUserLogin(
    val _id: String,
    val name: String,
    val email: String,
    val role: String
)*/

/**
 * Convert Network results to database objects
 */
fun NetworkContainerUserLogin.asDomainModel(): NetworkUser {
    return NetworkUser(
        _id = user._id,
        name = user.name,
        email = user.email,
        role = user.role,
        profilePic = user.profilePic)
}

/**
 * Convert Network results to database objects
 */
fun NetworkContainerUserLogin.asDatabaseModel(): User {
    return User(
        _id = user._id,
        name = user.name,
        email = user.email,
        role = user.role,
        profilePic = user.profilePic!!
    )
}

