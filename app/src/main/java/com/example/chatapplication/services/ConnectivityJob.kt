package com.example.chatapplication.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.example.chatapplication.db.OfflineSavedChatMessage
import com.example.chatapplication.viewmodels.MessagesViewModel
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService


class ConnectivityJob : JobService() {
    private lateinit var mContext: ConnectivityJob
    private lateinit var connectivityChange: BroadcastReceiver
    private lateinit var networkCallback: NetworkCallback
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate() {
        super.onCreate()
        mContext = this;
    }

    override fun onStartJob(job: JobParameters): Boolean {
        connectivityManager = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder().build(),
                object : NetworkCallback() {
                    // -Snip-
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    override fun onAvailable(network: Network?) {
                        println("onAvailable "+Thread.currentThread())
                        //val database: AppDatabase? = AppDatabase.getInstance(applicationContext)
                        //val allPendingMessages = database?.messageDao()?.loadAllMessages();
                        val toUserId = job.extras?.getString("toUserId");
                        val messagesViewModel: MessagesViewModel = MessagesViewModel(mContext);
                        Handler(Looper.getMainLooper()).post {
                            messagesViewModel.getPendingMessagesByToUserId(toUserId)
                                .observeForever(object : Observer<List<OfflineSavedChatMessage>> {
                                    override fun onChanged(pendingMessageOfflineSaveds: List<OfflineSavedChatMessage>) {
                                        // do something with stuff
                                        if (pendingMessageOfflineSaveds != null) {
                                            for (message in pendingMessageOfflineSaveds) {
                                                /*PushNotification(NotificationData(message.message)).also {
                                                    sendNotification(message.toId, it)
                                                }*/
                                            }
                                        }
                                    }
                                })
                        }
                    }

                    override fun onLost(network: Network?) {
                        println("onLost")
                    }
                }.also { networkCallback = it }
            )
        } else {
            registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent) {
                        handleConnectivityChange(
                            !intent.hasExtra("noConnectivity"),
                            intent.getIntExtra("networkType", -1)
                        )
                    }
                }.also { connectivityChange = it },
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        return true
    }

    override fun onStopJob(job: JobParameters): Boolean {
        println("onStopJob")
        if (networkCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) connectivityManager.unregisterNetworkCallback(
            networkCallback
        ) else if (connectivityChange != null) unregisterReceiver(connectivityChange)
        return true
    }

    private fun handleConnectivityChange(networkInfo: NetworkInfo) {
        // Calls handleConnectivityChange(boolean connected, int type)
    }

    private fun handleConnectivityChange(connected: Boolean, type: Int) {
        // Calls handleConnectivityChange(boolean connected, ConnectionType connectionType)
    }

    private fun handleConnectivityChange(connected: Boolean, connectionType: ConnectionType) {
        // Logic based on the new connection
    }

    private enum class ConnectionType {
        MOBILE, WIFI, VPN, OTHER
    }

    /*private fun sendNotification(toUserId: String, notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.notificationAPI.triggerNodeNotification(toUserId, notification)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val jsonObj = JSONObject(response.body()?.charStream()?.readText())
                        Toast.makeText(this@ConnectivityJob, jsonObj.getString("message"), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@ConnectivityJob, response.errorBody().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ConnectivityJob, e.message, Toast.LENGTH_LONG).show()
                }
            }
    }*/
}