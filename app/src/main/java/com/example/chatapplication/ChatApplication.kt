package com.example.chatapplication

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.work.Constraints
import androidx.work.NetworkType
import com.example.chatapplication.db.getInstance
import com.example.chatapplication.di.component.AppComponent
import com.example.chatapplication.di.component.DaggerAppComponent
import com.example.chatapplication.di.module.AppContextModule
import com.example.chatapplication.services.ConnectivityJob
import com.firebase.jobdispatcher.*


class ChatApplication : Application() {

    companion object {
        private lateinit var _appComponent: AppComponent
        var appComponent: AppComponent
            get() = _appComponent
            set(value) {
                _appComponent = value
            }

        private var activeActivity: Activity? = null
        fun getActiveActivity(): Activity? {
            return activeActivity
        }

        fun isAppOnForeground(context: Context): Boolean {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val appProcesses = activityManager.runningAppProcesses ?: return false
            val packageName = context.packageName
            for (appProcess in appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
                    return true
                }
            }
            return false
        }
    }

    override fun onCreate() {
        super.onCreate()
        _appComponent = DaggerAppComponent.builder()
            .appContextModule(AppContextModule(this))
            .build()
        _appComponent.inject(this)
        getInstance(this)
        //scheduleJob();
        setupActivityListener();
    }

    private fun setupActivityListener() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {

            }
            override fun onActivityResumed(activity: Activity) {
                activeActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                activeActivity = null
            }

            override fun onActivityStopped(activity: Activity) {

            }
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })
    }

    // Create charging constraint
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun scheduleJob() {
        val bundle = Bundle()
        bundle.putString("filtoUserIdename", "sdfsdf")

        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val job: Job = dispatcher.newJobBuilder()
                .setService(ConnectivityJob::class.java)
                //.setExtras(bundle)
                .setTag("connectivity-job")
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setReplaceCurrent(true)
                .setTrigger(Trigger.executionWindow(0, 0))
                .build()
            dispatcher.mustSchedule(job)
        }
    }
}