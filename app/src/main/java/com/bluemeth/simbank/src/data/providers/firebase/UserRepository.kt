package com.bluemeth.simbank.src.data.providers.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.model.ContactAgenda
import com.google.firebase.firestore.QueryDocumentSnapshot
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
        const val LEVEL_FIELD = "level"
        const val EXP_FIELD = "exp"
        const val IMAGE = "image"
        const val MISSIONS_COMPLETED_FIELD = "missions_completed"
        const val CONTACTS_FIELD = "contacts"
    }

    suspend fun createUserTable(userSignIn: UserSignIn) = runCatching {

        val user = User(
            userSignIn.email,
            userSignIn.password,
            userSignIn.nickName,
            userSignIn.phoneNumber.toInt(),
        )

        firebase.db
            .collection(USER_COLLECTION)
            .document(user.email)
            .set(user)
            .await()
    }.isSuccess

    fun getUserRecord(email: String): MutableLiveData<User> {
        val mutableData = MutableLiveData<User>()
        firebase.db
            .collection(USER_COLLECTION)
            .whereEqualTo(EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                mutableData.value = getUser(documents.first())
            }
            .addOnFailureListener { exception ->
                Timber.tag("Error").w(exception, "Error getting documents: ")
            }

        return mutableData
    }

    suspend fun getUsers(email: String): MutableLiveData<MutableList<User>> {
        val mutableData = MutableLiveData<MutableList<User>>()

        firebase.db
            .collection(USER_COLLECTION)
            .whereNotEqualTo(EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<User>()

                for (document in documents) {
                    listData.add(getUser(document))
                }
                mutableData.value = listData
            }
            .addOnFailureListener { exception ->
                Timber.tag("Error").w(exception, "Error getting documents: ")
            }
            .await()

        return mutableData
    }

    suspend fun getUserContacts(email: String): MutableLiveData<MutableList<User>> {
        val mutableData = MutableLiveData<MutableList<User>>()

        firebase.db
            .collection(USER_COLLECTION)
            .whereEqualTo(EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<User>()

                for (contact in documents.first().get(CONTACTS_FIELD) as MutableList<String>) {
                    firebase.db
                        .collection(USER_COLLECTION)
                        .whereEqualTo(EMAIL_FIELD, contact)
                        .get()
                        .addOnSuccessListener { user ->

                            listData.add(getUser(user.first()))
                        }
                }

                mutableData.value = listData
            }
            .addOnFailureListener { exception ->
                Timber.tag("Error").w(exception, "Error getting documents: ")
            }
            .await()

        return mutableData
    }

    fun findUserByEmail(email: String): MutableLiveData<User> {
        val user = MutableLiveData<User>()
        firebase.db
            .collection(USER_COLLECTION)
            .whereEqualTo(EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                user.value = getUser(documents.first())
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
                user.value = getUser(documents.first())
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
                user.value = getUser(documents.first())
            }
            .addOnFailureListener { exception ->
                Timber.tag("Failure").w(exception, "Error getting documents: ")
            }.await()

        return user
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
                user.value = getUser(documents.first())
            }
            .addOnFailureListener { exception ->
                Timber.tag("Failure").w(exception, "Error getting documents: ")
            }

        return user
    }

    suspend fun updateUserName(email: String, name: String) = runCatching {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .update(NAME_FIELD, name)
            .await()
    }.isSuccess

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

    suspend fun updateEmailContacts(oldEmail: String, newEmail: String) = runCatching {
        firebase.db
            .collection(USER_COLLECTION)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val contactsList = document.get(CONTACTS_FIELD) as MutableList<String>
                    val newList = mutableListOf<String>()
                    for (contact in contactsList) {
                        if (contact == oldEmail) {
                            newList.add(newEmail)
                        } else {
                            newList.add(contact)
                        }
                    }

                    firebase.db
                        .collection(USER_COLLECTION)
                        .document(document.id)
                        .update(CONTACTS_FIELD, newList)
                }
            }
            .await()
    }.isSuccess

    fun updateUserImage(email: String, image: String) {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .update(IMAGE, image)
    }

    fun updateUserMissionsCompleted(email: String, missionsCompleted: List<String>) {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .update(MISSIONS_COMPLETED_FIELD, missionsCompleted)
    }

    fun updateUserLevel(email: String, level: Int) {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .update(LEVEL_FIELD, level)
    }

    fun updateUserExperience(email: String, exp: Int) {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .update(EXP_FIELD, exp)
    }

    suspend fun updateUserContacts(email: String, contacts: List<String>) = runCatching {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .update(CONTACTS_FIELD, contacts)
            .await()
    }.isSuccess

    fun deleteUserByEmail(email: String) {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .delete()
    }

    suspend fun deleteUserContact(email: String, contactEmail: String) = runCatching {
        firebase.db
            .collection(USER_COLLECTION)
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                val listData = mutableListOf<String>()

                for (contact in document.get(CONTACTS_FIELD) as MutableList<String>) {
                    if (contact != contactEmail) {
                        listData.add(contact)
                    }
                }

                firebase.db
                    .collection(USER_COLLECTION)
                    .document(email)
                    .update(CONTACTS_FIELD, listData)
            }
            .await()
    }.isSuccess

    private fun getUser(document: QueryDocumentSnapshot): User {
        return User(
            document.getString(EMAIL_FIELD)!!,
            document.getString(PASSWORD_FIELD)!!,
            document.getString(NAME_FIELD)!!,
            document.getLong(PHONE_FIELD)!!.toInt(),
            document.getString(IMAGE)!!,
            document.getLong(LEVEL_FIELD)!!.toInt(),
            document.getLong(EXP_FIELD)!!.toInt(),
            document.get(MISSIONS_COMPLETED_FIELD) as MutableList<String>,
            document.get(CONTACTS_FIELD) as MutableList<String>
        )
    }
}