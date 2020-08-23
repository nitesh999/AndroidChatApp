package com.example.chatapplication.adapter

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.chatapplication.R
import com.example.chatapplication.util.Constants

/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError")
fun hideIfNetworkError(view: View, eventNetworkError: Boolean) {
    if(eventNetworkError) {
        view.visibility = View.GONE
    }
}

@BindingAdapter("userList")
fun hideIfUserListDataUnavailable(view: View, userList: Any?) {
    view.visibility = if (userList != null) View.GONE else View.VISIBLE
}

@BindingAdapter("userLogin")
fun hideIfUserLoginDataUnavailable(view: View, userLoginData: Any?) {
    view.visibility = if (userLoginData != null) View.GONE else View.VISIBLE
}

/**
 * Binding adapter used to display images from URL using Glide
 */
@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, profilePic: String) {
    Glide.with(imageView.context)
        .load("${Constants.BASE_URL}userMobile/photo/${profilePic}")
        .placeholder(R.mipmap.ic_launcher)
        .into(imageView)
}