package com.example.chatapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ItemUserBinding
import com.example.chatapplication.db.User
import kotlinx.android.synthetic.main.item_user.view.*

class UserListAdapter(val onUserClick: (User) -> Unit) : RecyclerView.Adapter<UserListViewHolder>() {

    /**
     * The users that our Adapter will show
     */
    var userList: List<User> = emptyList()
        set(value) {
            field = value
            // For an extra challenge, update this to use the paging library.

            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        UserClickHandlers(onUserClick)
        val withDataBinding: ItemUserBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), UserListViewHolder.LAYOUT, parent, false)
        return UserListViewHolder(withDataBinding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        //val user = userList[position]
        //holder.viewDataBinding.
        holder.viewDataBinding.let {
            it.user = userList[position]
            holder.itemView.ivImageUser.setOnClickListener(View.OnClickListener {
                onUserClick(userList[position])
                //holder.itemView.context.startActivity(Intent(holder.itemView.context, ChatActivity::class.java).putExtra("toUserId", outer.user._id))
            })
        }
    }

    inner class UserClickHandlers(val onUserClick: (User) -> Unit) {
        fun OnUserClick(user:User) {
            onUserClick(user)
        }
    }
}



/**
 * ViewHolder for DevByte items. All work is done by data binding.
 */
class UserListViewHolder(val viewDataBinding: ItemUserBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item_user
    }
}

