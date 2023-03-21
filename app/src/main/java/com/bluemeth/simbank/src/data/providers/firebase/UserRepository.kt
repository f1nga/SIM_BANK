package com.bluemeth.simbank.src.data.providers.firebase

import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.model.ContactAgenda
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
        const val IMAGE = "image"
    }

    suspend fun createUserTable(userSignIn: UserSignIn) = runCatching {

        val user = User(
            userSignIn.email,
            userSignIn.password,
            userSignIn.nickName,
            userSignIn.phoneNumber.toInt(),
            userSignIn.image
        )

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
                    documents.first().getString(IMAGE)!!
                )
            }
            .addOnFailureListener { exception ->
                Timber.tag("Failure").w(exception, "Error getting documents: ")
            }

        return user
    }

    fun findUserByName(name: String): MutableLiveData<User> {
        val user = MutableLiveData<User>()
        firebase.db
            .collection(USER_COLLECTION)
            .whereEqualTo(NAME_FIELD, name)
            .get()
            .addOnSuccessListener { documents ->
                user.value = User(
                    documents.first().getString(EMAIL_FIELD)!!,
                    documents.first().getString(PASSWORD_FIELD)!!,
                    documents.first().getString(NAME_FIELD)!!,
                    documents.first().getLong(PHONE_FIELD)!!.toInt(),
                    documents.first().getString(IMAGE)!!
                )
            }
            .addOnFailureListener { exception ->
                Timber.tag("Failure").w(exception, "Error getting documents: ")
            }

        return user
    }

    suspend fun findUserByName2(name: String): MutableLiveData<User> {
        val user = MutableLiveData<User>()
        firebase.db
            .collection(USER_COLLECTION)
            .whereEqualTo(NAME_FIELD, name)
            .get()
            .addOnSuccessListener { documents ->
                user.value = User(
                    documents.first().getString(EMAIL_FIELD)!!,
                    documents.first().getString(PASSWORD_FIELD)!!,
                    documents.first().getString(NAME_FIELD)!!,
                    documents.first().getLong(PHONE_FIELD)!!.toInt(),
                    documents.first().getString(IMAGE)!!
                )
            }
            .addOnFailureListener { exception ->
                Timber.tag("Failure").w(exception, "Error getting documents: ")
            }.await()

        return user
    }

    suspend fun findUserByNameList(names: List<String>): MutableLiveData<List<User>> {
        val users = MutableLiveData<List<User>>()

        for(name in names) {
            firebase.db
                .collection(USER_COLLECTION)
                .whereEqualTo(NAME_FIELD, name)
                .get()
                .addOnSuccessListener { documents ->
                    val list = mutableListOf<User>()
                    for(document in documents) {
                        list.add(
                            User(
                                document.getString(EMAIL_FIELD)!!,
                                document.getString(PASSWORD_FIELD)!!,
                                document.getString(NAME_FIELD)!!,
                                document.getLong(PHONE_FIELD)!!.toInt(),
                                document.getString(IMAGE)!!
                            )
                        )
                    }

                    users.value = list

                }
                .addOnFailureListener { exception ->
                    Timber.tag("Failure").w(exception, "Error getting documents: ")
                }.await()
        }


        return users
    }

    fun getContactUsers(email: String): MutableLiveData<MutableList<ContactAgenda>> {
        val mutableData = MutableLiveData<MutableList<ContactAgenda>>()

        firebase.db
            .collection(USER_COLLECTION)
            .whereNotEqualTo(EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<ContactAgenda>()

                for (document in documents) {

                    listData.add(
                        ContactAgenda(
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

    fun findUserByPhoneNumber(phoneNumber: Int): MutableLiveData<User> {
        val user = MutableLiveData<User>()
        firebase.db
            .collection(USER_COLLECTION)
            .whereEqualTo(PHONE_FIELD, phoneNumber)
            .get()
            .addOnSuccessListener { documents ->
                user.value = User(
                    documents.first().getString(EMAIL_FIELD)!!,
                    documents.first().getString(PASSWORD_FIELD)!!,
                    documents.first().getString(NAME_FIELD)!!,
                    documents.first().getLong(PHONE_FIELD)!!.toInt(),
                    documents.first().getString(IMAGE)!!
                )
            }
            .addOnFailureListener { exception ->
                Timber.tag("Failure").w(exception, "Error getting documents: ")
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

    fun updateUserImage(email:String, image: String){
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .update(IMAGE, image)
    }

    fun deleteUserByEmail(email: String) {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .delete()
    }
}