package com.bluemeth.simbank.src.data.providers.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.Transfer
import com.bluemeth.simbank.src.data.providers.firebase.MovementRepository.Companion.DATE_FIELD
import com.bluemeth.simbank.src.utils.Methods
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class TransferRepository @Inject constructor(private val firebase: FirebaseClient) {
    companion object {
        const val TRANSFERS_COLLECTION = "transfers"
        const val AMOUNT_FIELD = "amount"
        const val BENEFICIARY_IBAN_FIELD = "beneficiary_iban"
        const val BENEFICIARY_NAME_FIELD = "beneficiary_name"
        const val SUBJECT_FIELD = "subject"
        const val USER_EMAIL_FIELD = "user_email"
        const val IS_INCOME_FIELD = "income"
        const val REMAINING_MONEY_FIELD = "remaining_money"
    }

    suspend fun insertTransfer(transfer: Transfer) = runCatching {

        firebase.db
            .collection(TRANSFERS_COLLECTION)
            .document(Methods.generateToken())
            .set(transfer)
            .await()

    }.isSuccess

    fun getTransfersByEmail(email: String): LiveData<MutableList<Transfer>> {
        val transfersList = MutableLiveData<MutableList<Transfer>>()

        firebase.db.collection(TRANSFERS_COLLECTION)
            .whereEqualTo(USER_EMAIL_FIELD, email)
            .orderBy(DATE_FIELD, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<Transfer>()

                for (document in documents) {
                    listData.add(
                        Transfer(
                            document.getString(BENEFICIARY_IBAN_FIELD)!!,
                            document.getString(BENEFICIARY_NAME_FIELD)!!,
                            document.getDouble(AMOUNT_FIELD)!!,
                            document.getString(SUBJECT_FIELD)!!,
                            document.getTimestamp(DATE_FIELD)!!,
                            document.getBoolean(IS_INCOME_FIELD)!!,
                            document.getDouble(REMAINING_MONEY_FIELD)!!,
                            document.getString(USER_EMAIL_FIELD)!!
                        )
                    )
                }
                transfersList.value = listData
            }
            .addOnFailureListener { exception ->
                Timber.tag("HOOOL").w(exception, "Error getting documents: ")
            }

        return transfersList

    }
}