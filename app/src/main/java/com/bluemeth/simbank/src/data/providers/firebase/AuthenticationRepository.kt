package com.bluemeth.simbank.src.data.providers.firebase

import android.util.Log
import com.bluemeth.simbank.src.data.response.LoginResult
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthenticationRepository @Inject constructor(private val firebase: FirebaseClient) {

    val verifiedAccount: Flow<Boolean> = flow {
        while (true) {
            val verified = verifyEmailIsVerified()
            emit(verified)
            delay(1000)
        }
    }

    suspend fun login(email: String, password: String): LoginResult = runCatching {
        firebase.auth.signInWithEmailAndPassword(email, password).await()
    }.toLoginResult()

    suspend fun createAccount(email: String, password: String): AuthResult? {
        return firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun sendVerificationEmail() = runCatching {
        firebase.auth.currentUser?.sendEmailVerification()?.await()
    }.isSuccess

    private suspend fun verifyEmailIsVerified(): Boolean {
        firebase.auth.currentUser?.reload()?.await()
        return firebase.auth.currentUser?.isEmailVerified ?: false
    }

    private fun Result<AuthResult>.toLoginResult() = when (val result = getOrNull()) {
        null -> LoginResult.Error
        else -> {
            val userId = result.user
            checkNotNull(userId)
            LoginResult.Success(result.user?.isEmailVerified ?: false)
        }
    }

    fun logout() {
         firebase.auth.signOut()
    }

    suspend fun forgotPassword(email: String) = runCatching {
        firebase.auth.sendPasswordResetEmail(email).await()
    }.isSuccess

     fun updateEmail(email: String) {
        firebase.auth.currentUser!!.updateEmail(email)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("HOOOL", "User email address updated.")
            }
        }
    }

    fun updatePassword(password: String) {
        firebase.auth.currentUser!!.updatePassword(password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("HOOOL", "User email address updated.")
                }
            }
    }


}