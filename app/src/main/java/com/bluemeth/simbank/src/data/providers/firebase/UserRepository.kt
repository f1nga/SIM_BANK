package com.bluemeth.simbank.src.data.providers.firebase

import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.UserInitData
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(private val firebase: FirebaseClient) {

    companion object {
        const val USER_COLLECTION = "users"
    }

    suspend fun createUserTable(userSignIn: UserSignIn) = runCatching {

        val user = UserInitData.registerData(userSignIn)

        firebase.db
            .collection(USER_COLLECTION)
            .document(user.email)
            .set(user)
            .await()
    }.isSuccess
}