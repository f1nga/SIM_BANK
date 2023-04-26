package com.bluemeth.simbank.src.data.providers.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.utils.PaymentType
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
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
        const val REMAINING_MONEY_FIELD = "remaining_money"
        const val BENEFICIARY_REMAINING_MONEY_FIELD = "beneficiary_remaining_money"
        const val PAYMENT_TYPE_FIELD = "payment_type"
        const val DATE_FIELD = "date"
        const val CATEGORY_FIELD = "category"
        const val ID_FIELD = "id"
        const val REQUESTED_FIELD = "requested"

        const val BIZUM_TYPE = "Bizum"
        const val TRANSFER_TYPE = "Transfer"
    }

    suspend fun insertMovement(movement: Movement) = runCatching {
        firebase.db
            .collection(MOVEMENTS_COLLECTION)
            .document(movement.id)
            .set(movement)
            .await()

    }.isSuccess

    suspend fun getAllMovements(email: String, iban: String): LiveData<MutableList<Movement>> {
        val transfersList = MutableLiveData<MutableList<Movement>>()

        firebase.db.collection(MOVEMENTS_COLLECTION)
            .orderBy(DATE_FIELD, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<Movement>()

                for (document in documents) {
                    if(email == document.getString(USER_EMAIL_FIELD)) {
                        listData.add(getMovement(document, false))
                    } else if(iban == document.getString(BENEFICIARY_IBAN_FIELD)) {
                        listData.add(getMovement(document, true))
                    }
                }
                transfersList.value = listData

            }
            .addOnFailureListener { exception ->
                Timber.tag("HOOOL").w(exception, "Error getting documents: ")
            }.await()

        return transfersList

    }

    suspend fun getSendedMovements(email: String): LiveData<MutableList<Movement>> {
        val transfersList = MutableLiveData<MutableList<Movement>>()

        firebase.db.collection(MOVEMENTS_COLLECTION)
            .whereEqualTo(USER_EMAIL_FIELD, email)
            .orderBy(DATE_FIELD, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<Movement>()

                for (document in documents) {
                    listData.add(getMovement(document, false))

                }
                transfersList.value = listData
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
                    listData.add(getMovement(document, true))
                }

                transfersList.value = listData
            }
            .addOnFailureListener { exception ->
                Timber.tag("HOOOL").w(exception, "Error getting documents: ")
            }.await()

        return transfersList

    }

    suspend fun getMovementByID(id: String): LiveData<Movement> {
        val movement = MutableLiveData<Movement>()

        firebase.db.collection(MOVEMENTS_COLLECTION)
            .whereEqualTo(ID_FIELD, id)
            .get()
            .addOnSuccessListener { documents ->
                movement.value = getMovement(documents.first(), false)

            }
            .addOnFailureListener { exception ->
                Timber.tag("HOOOL").w(exception, "Error getting documents: ")
            }.await()

        return movement

    }

    suspend fun updateBeneficiaryName(name: String, newName: String) = runCatching {
        firebase.db.collection(MOVEMENTS_COLLECTION)
            .whereEqualTo(BENEFICIARY_NAME_FIELD, name)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    firebase.db.collection(MOVEMENTS_COLLECTION)
                        .document(document.id)
                        .update(BENEFICIARY_NAME_FIELD, newName)
                }
            }
            .await()
    }.isSuccess

    fun updateMovementUserEmail(email: String, newEmail: String) {
        firebase.db.collection(MOVEMENTS_COLLECTION)
            .whereEqualTo(USER_EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    firebase.db.collection(MOVEMENTS_COLLECTION)
                        .document(document.id)
                        .update(USER_EMAIL_FIELD, newEmail)
                }
            }
    }

    suspend fun getTotalMovementsByUser(email: String, name: String): MutableLiveData<Int> {
        val totalMovements = MutableLiveData<Int>()

        firebase.db.collection(MOVEMENTS_COLLECTION)
            .whereEqualTo(USER_EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                var total = documents.size()
                firebase.db.collection(MOVEMENTS_COLLECTION)
                    .whereEqualTo(BENEFICIARY_NAME_FIELD, name)
                    .get()
                    .addOnSuccessListener { documents2 ->
                        total += documents2.size()
                        totalMovements.value = total
                    }
                totalMovements.value = documents.size()
            }
            .await()

        return totalMovements
    }

    private fun getMovement(document: QueryDocumentSnapshot, isIncome: Boolean): Movement {
        val paymentType =
            when (document.getString(PAYMENT_TYPE_FIELD)) {
                TRANSFER_TYPE -> PaymentType.Transfer
                BIZUM_TYPE -> PaymentType.Bizum
                else -> PaymentType.Target_pay
            }

        return Movement(
            id = document.getString(ID_FIELD)!!,
            beneficiary_iban = document.getString(BENEFICIARY_IBAN_FIELD)!!,
            beneficiary_name = document.getString(BENEFICIARY_NAME_FIELD)!!,
            amount = document.getDouble(AMOUNT_FIELD)!!,
            subject = document.getString(SUBJECT_FIELD)!!,
            category = document.getString(CATEGORY_FIELD)!!,
            date = document.getTimestamp(DATE_FIELD)!!,
            isIncome,
            payment_type = paymentType,
            requested = document.getBoolean(REQUESTED_FIELD)!!,
            remaining_money = document.getDouble(REMAINING_MONEY_FIELD)!!,
            beneficiary_remaining_money = document.getDouble(BENEFICIARY_REMAINING_MONEY_FIELD)!!,
            user_email = document.getString(USER_EMAIL_FIELD)!!
        )
    }
}