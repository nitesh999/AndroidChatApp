package com.example.chatapplication.services

import android.content.*
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.chatapplication.ChatApplication
import com.example.chatapplication.data.NotificationData
import com.example.chatapplication.data.PushNotification
import com.example.chatapplication.db.OfflineSavedChatMessage
import com.example.chatapplication.util.Constants
import com.example.chatapplication.viewmodels.MessagesViewModel
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class ConnectivityJob : JobService() {
    private lateinit var mContext: ConnectivityJob
    private lateinit var connectivityChange: BroadcastReceiver
    private lateinit var networkCallback: NetworkCallback
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var mSharedPreference: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        mContext = this;
        mSharedPreference = mContext.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
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
                        val messagesViewModel = MessagesViewModel(mContext);
                        val listOfflineSavedChatMessage : List<OfflineSavedChatMessage> = messagesViewModel.getPendingMessagesByToUserId(toUserId)
                        // do something with stuff
                        if (listOfflineSavedChatMessage != null) {
                            for (message in listOfflineSavedChatMessage) {
                                PushNotification(NotificationData(message.message)).let {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val userToken = mSharedPreference.getString("userToken", "")
                                        val bearerToken = "Bearer $userToken"
                                        val response = ChatApplication.appComponent.getNetworkHelper().notificationAPI
                                            .triggerNodeNotification(bearerToken, message.toId, it)
                                        if (response.isSuccessful) {
                                            messagesViewModel.deletePendingMessageById(message.messageId)
                                            val jsonObj = JSONObject(response.body()?.charStream()?.readText())
                                        } else {
                                        }
                                    }
                                }
                            }
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
}