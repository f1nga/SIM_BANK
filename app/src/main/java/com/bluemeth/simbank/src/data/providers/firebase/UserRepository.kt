package com.bluemeth.simbank.src.data.providers.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.UserInitData
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(private val firebase: FirebaseClient) {

    companion object {
        const val USER_COLLECTION = "users"
        const val EMAIL_FIELD = "email"
    }

    suspend fun createUserTable(userSignIn: UserSignIn) = runCatching {

        val user = UserInitData.registerData(userSignIn)

        firebase.db
            .collection(USER_COLLECTION)
            .document(user.email)
            .set(user)
            .await()
    }.isSuccess

    fun findUserByEmail(email: String): User  {
        val users = MutableLiveData<MutableList<User>>()

        firebase.db.collection(USER_COLLECTION)
            .whereEqualTo(EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<User>()

                for (document in documents) {
                    Log.i("reposii", document.getString("email")!!)
                    listData.add(User(document.getString("email")!!, document.getString("name")!!, 23 ))
                }

                users.value = listData

            }
            .addOnFailureListener { exception ->
                Log.w("HOOOL", "Error getting documents: ", exception)
            }

        return users.value!![0]
    }
}