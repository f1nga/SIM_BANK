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
        const val PASSWORD_FIELD = "password"
        const val NAME_FIELD = "name"
        const val PHONE_FIELD = "phone"
    }

    suspend fun createUserTable(userSignIn: UserSignIn) = runCatching {

        val user = UserInitData.registerData(userSignIn)

        firebase.db
            .collection(USER_COLLECTION)
            .document(user.email)
            .set(user)
            .await()
    }.isSuccess

    fun findUserByEmail(email: String): MutableLiveData<User> {
        val user = MutableLiveData<User>()
        Log.i("holde", email)
        firebase.db
            .collection(USER_COLLECTION)
            .whereEqualTo(EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                user.value = User(
                    documents.first().getString(EMAIL_FIELD)!!,
                    documents.first().getString(PASSWORD_FIELD)!!,
                    documents.first().getString(NAME_FIELD)!!,
                    documents.first().getLong(PHONE_FIELD)!!.toInt(),
                )
            }
            .addOnFailureListener { exception ->
                Log.w("Failure", "Error getting documents: ", exception)
            }

        return user
    }

    fun updateUserName(email: String, name: String) {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .update(NAME_FIELD, name)
    }

    fun updateUserPhone(email: String, phone: Int) {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .update(PHONE_FIELD, phone)
    }

    fun updateUserPassword(email: String, password: String) {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .update(PASSWORD_FIELD, password)
    }

    fun updateUserEmail(email: String, newUser: User) {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email).delete()

        firebase.db
            .collection(USER_COLLECTION)
            .document(newUser.email)
            .set(newUser)
    }
}