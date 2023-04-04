package com.bluemeth.simbank.src.data.providers.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.RequestedBizum
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class RequestedBizumRepository @Inject constructor(private val firebase: FirebaseClient) {

    private companion object {
        const val REQUESTED_BIZUMS_COLLECTION = "requested_bizums"

        const val AMOUNT_FIELD = "amount"
        const val BENEFICIARY_IBAN_FIELD = "beneficiary_iban"
        const val BENEFICIARY_NAME_FIELD = "beneficiary_name"
        const val SUBJECT_FIELD = "subject"
        const val DATE_FIELD = "date"
        const val ID_FIELD = "id"
        const val USER_EMAIL_FIELD = "user_email"

    }

    suspend fun insertRequestedBizum(requestedBizum: RequestedBizum) = runCatching {
        firebase.db
            .collection(REQUESTED_BIZUMS_COLLECTION)
            .document(requestedBizum.id)
            .set(requestedBizum)
            .await()

    }.isSuccess

    suspend fun getRequestedBizumByID(id: String): LiveData<RequestedBizum> {
        val requestedBizum = MutableLiveData<RequestedBizum>()

        firebase.db.collection(REQUESTED_BIZUMS_COLLECTION)
            .whereEqualTo(ID_FIELD, id)
            .get()
            .addOnSuccessListener { documents ->

                requestedBizum.value = getRequestedBizum(documents.first())

            }
            .addOnFailureListener { exception ->
                Timber.tag("HOOOL").w(exception, "Error getting documents: ")
            }.await()

        return requestedBizum

    }

    suspend fun deleteRequestedBizum(id: String) = runCatching {
        firebase.db
            .collection(REQUESTED_BIZUMS_COLLECTION)
            .document(id)
            .delete()
            .await()

    }.isSuccess

    private fun getRequestedBizum(document: QueryDocumentSnapshot): RequestedBizum {
        return RequestedBizum(
            id = document.getString(ID_FIELD)!!,
            beneficiary_iban = document.getString(BENEFICIARY_IBAN_FIELD)!!,
            beneficiary_name = document.getString(BENEFICIARY_NAME_FIELD)!!,
            amount = document.getDouble(AMOUNT_FIELD)!!,
            subject = document.getString(SUBJECT_FIELD)!!,
            date = document.getTimestamp(DATE_FIELD)!!,
            user_email = document.getString(USER_EMAIL_FIELD)!!
        )
    }
 }