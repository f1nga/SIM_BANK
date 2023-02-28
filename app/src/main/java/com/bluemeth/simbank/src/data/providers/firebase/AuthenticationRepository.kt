package com.bluemeth.simbank.src.data.providers.firebase

import com.bluemeth.simbank.src.data.response.LoginResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
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
        Timber.tag("emaailRepo").i(firebase.auth.currentUser!!.email!!)
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

    fun getCurrentUser(): FirebaseUser {
        return firebase.auth.currentUser!!
    }

    suspend fun forgotPassword(email: String) = runCatching {
        firebase.auth.sendPasswordResetEmail(email).await()
    }.isSuccess

    suspend fun updateEmail(email: String) = runCatching {
        firebase.auth.currentUser!!.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.tag("CORRECT").d("User email address updated.")
                }
            }.await()
    }.isSuccess

    suspend fun updatePassword(password: String) = runCatching {
        firebase.auth.currentUser!!.updatePassword(password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.tag("CORRECT").d("User password updated.")
                }
            }.await()
    }.isSuccess

    suspend fun deleteAccount() = runCatching{
        firebase.auth.currentUser!!.delete().await()
    }.isSuccess
}