package com.bluemeth.simbank.src.data.repositories

import android.app.Activity
import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationRepository {
    companion object {
        private val auth = Firebase.auth

        fun register(email: String, password: String) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                            Log.d(ContentValues.TAG, "createUserWithEmail:success")
                        }
                    } else {
                        Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    }
                }
        }
    }
}