package com.example.chatapplication.di.helper

import android.content.Context
import android.content.SharedPreferences
import com.example.chatapplication.util.Constants
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PreferencesHelper {

    private var preferences: SharedPreferences

    @Inject
    constructor(@Named("appcontext") context: Context) {
        preferences = context.getSharedPreferences(
            Constants.SHARED_PREFERENCE,
            Context.MODE_PRIVATE
        )
    }

    private val editor: SharedPreferences.Editor
        private get() = preferences!!.edit()

    fun getString(key: String?, vararg defaultValue: String?): String? {
        return preferences!!.getString(
            key,
            if (defaultValue == null || defaultValue.size == 0) "" else defaultValue[0]
        )
    }

    fun putString(key: String?, value: String?) {
        editor.putString(key, value).apply()
    }

    fun getLong(key: String?, vararg defaultValue: Long): Long {
        return preferences!!.getLong(key,
            if (defaultValue.size == 0) 0 else defaultValue[0]
        )
    }

    fun putLong(key: String?, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun getBoolean(key: String?, vararg defaultValue: Boolean): Boolean {
        return preferences!!.getBoolean(
            key,
            defaultValue.size != 0 && defaultValue[0]
        )
    }

    fun putBoolean(key: String?, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun getInt(key: String?, vararg defaultValue: Int): Int {
        return preferences!!.getInt(
            key,
            if (defaultValue.size == 0) 0 else defaultValue[0]
        )
    }

    fun putInt(key: String?, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun remove(key: String?) {
        editor.remove(key).apply()
    }

    fun clear() {
        editor.clear().apply()
    }

}
