package com.example.chatapplication.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.chatapplication.R
import com.example.chatapplication.adapter.ChatListAdapter
import com.example.chatapplication.data.NotificationData
import com.example.chatapplication.databinding.ActivityChatBinding
import com.example.chatapplication.util.Constants
import com.example.chatapplication.viewmodels.ChatMessageViewModel
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    lateinit var chatMessageViewModel: ChatMessageViewModel
    lateinit var chatListAdapter:ChatListAdapter;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.lifecycleOwner = this

        chatMessageViewModel = ViewModelProviders.of(this).get(ChatMessageViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = chatMessageViewModel
        binding.executePendingBindings()

        chatListAdapter = ChatListAdapter()
        val linearLayoutManager = LinearLayoutManager(application)
        linearLayoutManager.reverseLayout = true
        binding.rvChatList.apply{
            layoutManager = linearLayoutManager
            adapter = chatListAdapter
        }

        val toUserId = intent.extras?.getString("toUserId", "")

        btnSend.setOnClickListener {
            val message = etMessage.text.toString()
            if (toUserId != null) {
                chatMessageViewModel.sendChatMessage(message, toUserId)
            }
        }

        chatMessageViewModel.eventOfflineNetworkListener.observe(this,
            Observer<Boolean> { isNetworkError ->
                if (isNetworkError)
                    startListeningSentMessageStatus()
            })

        /*chatMessageViewModel.messagesList.observe(this,
            Observer<NotificationData> {
                Log.d("Your added data : ", it.message)
            })*/
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(listener, IntentFilter("APPEND_MESSAGE"))
    }

    private fun startListeningSentMessageStatus() {
        WorkManager.getInstance(this)
            .getWorkInfoByIdLiveData(chatMessageViewModel.offlineChatWork.id)
            .observe(this, Observer { info ->
                if (info != null && info.state.isFinished) {
                    val myResult = info.outputData.getString(Constants.KEY_RESULT)
                    chatMessageViewModel.eventOfflineNetworkListener.value = false
                    Toast.makeText(applicationContext, myResult, Toast.LENGTH_LONG).show()
                    if (info.state === WorkInfo.State.SUCCEEDED)
                        WorkManager.getInstance(this).cancelWorkById(chatMessageViewModel.offlineChatWork.id)
                    // ... do something with the result ...
                }
        })
    }

    private val listener = MyBroadcastReceiver()
    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent){
            when (intent.action) {
                "APPEND_MESSAGE" -> {
                    val message = intent.getStringExtra("MESSAGE")
                    addItems(NotificationData(message))
                    Log.d("Your Received data : ", message)
                }
                else ->
                    Toast.makeText(context, "Action Not Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun addItems(newItem: NotificationData) {
        val position: Int = chatListAdapter.notificationChatList.size + 1
        chatListAdapter.notificationChatList.add(newItem)
        //chatListAdapter.notifyItemInserted(position)
        chatListAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener)
    }
}
