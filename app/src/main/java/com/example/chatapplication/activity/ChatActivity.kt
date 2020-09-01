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

        chatMessageViewModel = ViewModelProviders.of(this, ChatMessageViewModel.Factory()).get(ChatMessageViewModel::class.java)
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
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(listener, IntentFilter("APPEND_MESSAGE"))
    }

    private fun startListeningSentMessageStatus() {
        WorkManager.getInstance(applicationContext)
            .getWorkInfoByIdLiveData(chatMessageViewModel.offlineChatWork.id)
            .observe(this, Observer { info ->
                if (info != null && info.state.isFinished) {
                    val myResult = info.outputData.getString(Constants.KEY_RESULT)
                    chatMessageViewModel.eventOfflineNetworkListener.value = false
                    Toast.makeText(applicationContext, myResult, Toast.LENGTH_LONG).show()
                    if (info.state === WorkInfo.State.SUCCEEDED) {
                    }
                    WorkManager.getInstance(applicationContext).cancelUniqueWork(Constants.IS_OFFLINE_PENDING_MESSAGE)
                }
        })
    }

    private val listener = NotificationBroadcastReceiver()
    inner class NotificationBroadcastReceiver : BroadcastReceiver() {
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
        chatListAdapter.notificationChatList.add(newItem)
        chatListAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener)
    }
}
