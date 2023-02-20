package com.bluemeth.simbank.src.data.providers

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class Prefs(context: Context) {
    val SHARED_PREFERENCE_NAME = "authDB"
    val TOKEN_KEY = "token"
    val EMAIL_KEY = "email"
    val PASSWORD_KEY = "password"
    val DEFAULT_VALUE = ""

    private val storage: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0)

     fun saveUser(email: String, password: String) {
         storage.edit().putString(TOKEN_KEY, generateToken()).apply()
         storage.edit().putString(EMAIL_KEY, email).apply()
         storage.edit().putString(PASSWORD_KEY, password).apply()
     }

    fun getToken(): String {
        return storage.getString(TOKEN_KEY, DEFAULT_VALUE)!!
    }

    fun getEmail(): String {
        return storage.getString(EMAIL_KEY, DEFAULT_VALUE)!!
    }

    fun getPassword(): String {
        return storage.getString(PASSWORD_KEY, DEFAULT_VALUE)!!
    }

    fun clearPrefs() {
        storage.edit().clear().apply()
    }

    private fun generateToken() : String{
        return UUID.randomUUID().toString()
    }
}