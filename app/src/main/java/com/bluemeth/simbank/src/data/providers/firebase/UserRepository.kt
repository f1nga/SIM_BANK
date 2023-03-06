package com.bluemeth.simbank.src.data.providers.firebase

import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.models.utils.CreditCardType
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.UserAddFromAgenda
import kotlinx.coroutines.tasks.await
import timber.log.Timber
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

        val user = User(userSignIn.email, userSignIn.password, userSignIn.nickName,userSignIn.phoneNumber.toInt())

        firebase.db
            .collection(USER_COLLECTION)
            .document(user.email)
            .set(user)
            .await()
    }.isSuccess

    fun findUserByEmail(email: String): MutableLiveData<User> {
        val user = MutableLiveData<User>()
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
                Timber.tag("Failure").w(exception, "Error getting documents: ")
            }

        return user
    }

    fun getContactUsers(email: String): MutableLiveData<MutableList<UserAddFromAgenda>> {
        val mutableData = MutableLiveData<MutableList<UserAddFromAgenda>>()

        firebase.db
            .collection(USER_COLLECTION)
            .whereNotEqualTo(EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<UserAddFromAgenda>()

                for (document in documents) {

                    listData.add(
                       UserAddFromAgenda(
                           name = document.getString(NAME_FIELD)!!,
                           phoneNumber = document.getLong(PHONE_FIELD)!!.toInt()
                       )
                    )
                }
                mutableData.value = listData
            }
            .addOnFailureListener { exception ->
                Timber.tag("Error").w(exception, "Error getting documents: ")
            }

        return mutableData
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

    fun deleteUserByEmail(email: String) {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .delete()
    }
}