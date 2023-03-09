package com.bluemeth.simbank.src.data.providers.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.bluemeth.simbank.src.data.providers.firebase.TargetPayRepository.Companion.DATE_FIELD
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
        const val PAYMENT_TYPE_FIELD = "payment_type"

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

    fun getMovementsByType(email: String, type: String): LiveData<MutableList<Movement>> {
        val transfersList = MutableLiveData<MutableList<Movement>>()

        firebase.db.collection(MOVEMENTS_COLLECTION)
            .whereEqualTo(USER_EMAIL_FIELD, email)
            .orderBy(DATE_FIELD, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<Movement>()

                for (document in documents) {
                    if (document.getString(PAYMENT_TYPE_FIELD) == type) {
                        val paymentType =
                            when (document.getString(PAYMENT_TYPE_FIELD)) {
                                TRANSFER_TYPE -> PaymentType.Transfer
                                BIZUM_TYPE -> PaymentType.Bizum
                                else -> PaymentType.Target_pay
                            }

                        listData.add(
                            Movement(
                                document.getString(BENEFICIARY_IBAN_FIELD)!!,
                                document.getString(BENEFICIARY_NAME_FIELD)!!,
                                document.getDouble(AMOUNT_FIELD)!!,
                                document.getString(SUBJECT_FIELD)!!,
                                document.getTimestamp(DATE_FIELD)!!,
                                document.getBoolean(IS_INCOME_FIELD)!!,
                                paymentType,
                                document.getDouble(REMAINING_MONEY_FIELD)!!,
                                document.getString(USER_EMAIL_FIELD)!!
                            )
                        )
                    } else if (type == "All"){
                        val paymentType =
                            when (document.getString(PAYMENT_TYPE_FIELD)) {
                                TRANSFER_TYPE -> PaymentType.Transfer
                                BIZUM_TYPE -> PaymentType.Bizum
                                else -> PaymentType.Target_pay
                            }

                        listData.add(
                            Movement(
                                document.getString(BENEFICIARY_IBAN_FIELD)!!,
                                document.getString(BENEFICIARY_NAME_FIELD)!!,
                                document.getDouble(AMOUNT_FIELD)!!,
                                document.getString(SUBJECT_FIELD)!!,
                                document.getTimestamp(DATE_FIELD)!!,
                                document.getBoolean(IS_INCOME_FIELD)!!,
                                paymentType,
                                document.getDouble(REMAINING_MONEY_FIELD)!!,
                                document.getString(USER_EMAIL_FIELD)!!
                            )
                        )
                    }
                    transfersList.value = listData
                }
            }
            .addOnFailureListener { exception ->
                Timber.tag("HOOOL").w(exception, "Error getting documents: ")
            }

        return transfersList

    }

    fun getMovementsByIsIncome(email: String, type: String, isIncome: Boolean): LiveData<MutableList<Movement>> {
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

                    if (document.getString(PAYMENT_TYPE_FIELD) == type) {
                        if(document.getString(PAYMENT_TYPE_FIELD) == BIZUM_TYPE) {
                            if(!isIncome) {
                                if(!document.getBoolean(IS_INCOME_FIELD)!!) {
                                    listData.add(
                                        Movement(
                                            document.getString(BENEFICIARY_IBAN_FIELD)!!,
                                            document.getString(BENEFICIARY_NAME_FIELD)!!,
                                            document.getDouble(AMOUNT_FIELD)!!,
                                            document.getString(SUBJECT_FIELD)!!,
                                            document.getTimestamp(DATE_FIELD)!!,
                                            document.getBoolean(IS_INCOME_FIELD)!!,
                                            paymentType,
                                            document.getDouble(REMAINING_MONEY_FIELD)!!,
                                            document.getString(USER_EMAIL_FIELD)!!
                                        )
                                    )
                                }
                            } else {
                                if(document.getBoolean(IS_INCOME_FIELD)!!) {
                                    listData.add(
                                        Movement(
                                            document.getString(BENEFICIARY_IBAN_FIELD)!!,
                                            document.getString(BENEFICIARY_NAME_FIELD)!!,
                                            document.getDouble(AMOUNT_FIELD)!!,
                                            document.getString(SUBJECT_FIELD)!!,
                                            document.getTimestamp(DATE_FIELD)!!,
                                            document.getBoolean(IS_INCOME_FIELD)!!,
                                            paymentType,
                                            document.getDouble(REMAINING_MONEY_FIELD)!!,
                                            document.getString(USER_EMAIL_FIELD)!!
                                        )
                                    )
                                }
                            }

                        }

                    } else if (type == "All"){
                        listData.add(
                            Movement(
                                document.getString(BENEFICIARY_IBAN_FIELD)!!,
                                document.getString(BENEFICIARY_NAME_FIELD)!!,
                                document.getDouble(AMOUNT_FIELD)!!,
                                document.getString(SUBJECT_FIELD)!!,
                                document.getTimestamp(DATE_FIELD)!!,
                                document.getBoolean(IS_INCOME_FIELD)!!,
                                paymentType,
                                document.getDouble(REMAINING_MONEY_FIELD)!!,
                                document.getString(USER_EMAIL_FIELD)!!
                            )
                        )
                    }
                    transfersList.value = listData
                }
            }
            .addOnFailureListener { exception ->
                Timber.tag("HOOOL").w(exception, "Error getting documents: ")
            }

        return transfersList

    }
}