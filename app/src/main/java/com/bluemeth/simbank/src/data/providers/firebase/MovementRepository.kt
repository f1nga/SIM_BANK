package com.bluemeth.simbank.src.data.providers.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.Bizum
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.utils.Methods
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class MovementRepository @Inject constructor(private val firebase: FirebaseClient) {
    companion object {
        const val MOVEMENTS_COLLECTION = "movements"

        const val AMOUNT_FIELD = "amount"
        const val BENEFICIARY_IBAN_FIELD = "beneficiary_iban"
        const val BENEFICIARY_NAME_FIELD = "beneficiary_name"
        const val SUBJECT_FIELD = "subject"
        const val USER_EMAIL_FIELD = "user_email"
        const val IS_INCOME_FIELD = "income"
        const val REMAINING_MONEY_FIELD = "remaining_money"
        const val BENEFICIARY_REMAINING_MONEY_FIELD = "beneficiary_remaining_money"
        const val PAYMENT_TYPE_FIELD = "payment_type"
        const val DATE_FIELD = "date"
        const val CATEGORY_FIELD = "category"
        const val ID_FIELD = "id"

        const val BIZUM_TYPE = "Bizum"
        const val TRANSFER_TYPE = "Transfer"
    }

    suspend fun insertMovement(movement: Movement) = runCatching {
        firebase.db
            .collection(MOVEMENTS_COLLECTION)
            .document(Methods.generateToken())
            .set(movement)
            .await()

    }.isSuccess

    suspend fun insertMovement(movement: Bizum) = runCatching {
        firebase.db
            .collection(MOVEMENTS_COLLECTION)
            .document(Methods.generateToken())
            .set(movement)
            .await()

    }.isSuccess

    suspend fun getSendedMovements(email: String): LiveData<MutableList<Movement>> {
        val transfersList = MutableLiveData<MutableList<Movement>>()

        firebase.db.collection(MOVEMENTS_COLLECTION)
            .whereEqualTo(USER_EMAIL_FIELD, email)
            .orderBy(DATE_FIELD, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<Movement>()

                for (document in documents) {
                        val paymentType =
                            when (document.getString(PAYMENT_TYPE_FIELD)) {
                                TRANSFER_TYPE -> PaymentType.Transfer
                                BIZUM_TYPE -> PaymentType.Bizum
                                else -> PaymentType.Target_pay
                            }

                        listData.add(
                            Movement(
                                document.getString(ID_FIELD)!!,
                                document.getString(BENEFICIARY_IBAN_FIELD)!!,
                                document.getString(BENEFICIARY_NAME_FIELD)!!,
                                document.getDouble(AMOUNT_FIELD)!!,
                                document.getString(SUBJECT_FIELD)!!,
                                document.getString(CATEGORY_FIELD)!!,
                                document.getTimestamp(DATE_FIELD)!!,
                                document.getBoolean(IS_INCOME_FIELD)!!,
                                paymentType,
                                document.getDouble(REMAINING_MONEY_FIELD)!!,
                                document.getDouble(BENEFICIARY_REMAINING_MONEY_FIELD)!!,
                                document.getString(USER_EMAIL_FIELD)!!
                            )
                        )

                    transfersList.value = listData
                }
            }
            .addOnFailureListener { exception ->
                Timber.tag("HOOOL").w(exception, "Error getting documents: ")
            }.await()

        return transfersList

    }

    suspend fun getReceivedMovements(iban: String): LiveData<MutableList<Movement>> {
        val transfersList = MutableLiveData<MutableList<Movement>>()

        firebase.db.collection(MOVEMENTS_COLLECTION)
            .whereEqualTo(BENEFICIARY_IBAN_FIELD, iban)
            .orderBy(DATE_FIELD, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<Movement>()

                for (document in documents) {
                    val paymentType =
                        when (document.getString(PAYMENT_TYPE_FIELD)) {
                            TRANSFER_TYPE -> PaymentType.Transfer
                            BIZUM_TYPE -> PaymentType.Bizum
                            else -> PaymentType.Target_pay
                        }

                    listData.add(
                        Movement(
                            document.getString(ID_FIELD)!!,
                            document.getString(BENEFICIARY_IBAN_FIELD)!!,
                            document.getString(BENEFICIARY_NAME_FIELD)!!,
                            document.getDouble(AMOUNT_FIELD)!!,
                            document.getString(SUBJECT_FIELD)!!,
                            document.getString(CATEGORY_FIELD)!!,
                            document.getTimestamp(DATE_FIELD)!!,
                            true,
                            paymentType,
                            document.getDouble(REMAINING_MONEY_FIELD)!!,
                            document.getDouble(BENEFICIARY_REMAINING_MONEY_FIELD)!!,
                            document.getString(USER_EMAIL_FIELD)!!
                        )
                    )

                    transfersList.value = listData
                }
            }
            .addOnFailureListener { exception ->
                Timber.tag("HOOOL").w(exception, "Error getting documents: ")
            }.await()

        return transfersList

    }
}