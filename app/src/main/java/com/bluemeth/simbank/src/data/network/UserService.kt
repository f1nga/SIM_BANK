package com.bluemeth.simbank.src.data.network

import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserService @Inject constructor(private val firebase: FirebaseClient) {

    companion object {
        const val USER_COLLECTION = "users"
    }

    suspend fun createUserTable(userSignIn: UserSignIn) = runCatching {

        val user1 = hashMapOf(
            "email" to userSignIn.email,
            "nickname" to userSignIn.nickName,
        )

       // val user = User(userSignIn.email, userSignIn.nickName, /*userSignIn.phoneNumber*/)


        firebase.db
            .collection(USER_COLLECTION)
            .add(user1).await()

    }.isSuccess
}