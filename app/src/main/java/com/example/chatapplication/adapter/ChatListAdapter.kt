package com.example.chatapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.data.NotificationData
import com.example.chatapplication.databinding.ItemChatBinding

class ChatListAdapter: RecyclerView.Adapter<ChatListViewHolder>() {

    /**
     * The users that our Adapter will show
     */
    var notificationChatList: MutableList<NotificationData> = mutableListOf()
        set(value) {
            field = value
            // For an extra challenge, update this to use the paging library.

            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val withDataBinding: ItemChatBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), ChatListViewHolder.LAYOUT, parent, false)
        return ChatListViewHolder(withDataBinding)
    }

    override fun getItemCount(): Int {
        return notificationChatList.size
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.viewDataBinding.let {
            it.chatNotificationMessage = notificationChatList[position]
        }
    }
}

/**
 * ViewHolder for DevByte items. All work is done by data binding.
 */
class ChatListViewHolder(val viewDataBinding: ItemChatBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item_chat
    }
}
