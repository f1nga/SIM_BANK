package com.bluemeth.simbank.src.data.providers

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class Prefs(context: Context) {

    companion object {
        const val SHARED_PREFERENCE_NAME = "authDB"
        const val TOKEN_KEY = "token"
        const val EMAIL_KEY = "email"
        const val PASSWORD_KEY = "password"
        const val STEPS_KEY = "steps"
        const val DEFAULT_VALUE = ""
    }

    private val storage: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, 0)

     fun saveUser(email: String, password: String) {
         storage.edit().putString(EMAIL_KEY, email).apply()
         storage.edit().putString(PASSWORD_KEY, password).apply()
     }

    fun saveToken() {
        storage.edit().putString(TOKEN_KEY, generateToken()).apply()
    }

    fun saveSteps() {
        storage.edit().putString(STEPS_KEY, generateToken()).apply()
    }

    fun getSteps() = storage.getString(STEPS_KEY, DEFAULT_VALUE)!!

    fun getToken() = storage.getString(TOKEN_KEY, DEFAULT_VALUE)!!

    fun getEmail() = storage.getString(EMAIL_KEY, DEFAULT_VALUE)!!

    fun getPassword() = storage.getString(PASSWORD_KEY, DEFAULT_VALUE)!!

    fun clearPrefs() = storage.edit().clear().apply()

    private fun generateToken() = UUID.randomUUID().toString()
}