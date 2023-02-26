package com.bluemeth.simbank.src.data.providers.firebase

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.utils.CreditCardType
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class CreditCardRepository @Inject constructor(private val firebase: FirebaseClient){

    companion object {
        const val CREDIT_CARDS_COLLECTION = "credit_cards"
        const val BANK_IBAN_FIELD = "bank_iban"
        const val CADUCITY_FIELD = "caducity"
        const val CVV_FIELD = "cvv"
        const val NUMBER_FIELD = "number"
        const val PIN_FIELD = "pin"
        const val MONEY_FIELD = "money"
        const val TYPE_FIELD = "type"
        const val DEBIT_CARD = "Debito"
        const val CREDIT_CARD = "Credito"
    }

    suspend fun insertCreditCard(creditCard: CreditCard) = runCatching {
        firebase.db
            .collection(CREDIT_CARDS_COLLECTION)
            .document(creditCard.number)
            .set(creditCard)
            .await()

    }.isSuccess

    fun getCreditCards(iban: String) : LiveData<MutableList<CreditCard>> {
        val mutableData = MutableLiveData<MutableList<CreditCard>>()

        firebase.db.collection(CREDIT_CARDS_COLLECTION)
            .whereEqualTo(BANK_IBAN_FIELD, iban)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<CreditCard>()

                for (document in documents) {
                    val creditCardType =
                        when(document.getString(TYPE_FIELD)) {
                            CREDIT_CARD -> CreditCardType.Credito
                            DEBIT_CARD -> CreditCardType.Debito
                            else ->  CreditCardType.Prepago
                        }

                    listData.add(
                        CreditCard(
                            document.getString(NUMBER_FIELD)!!,
                            document.getDouble(MONEY_FIELD)!!,
                            document.getLong(PIN_FIELD)!!.toInt(),
                            document.getLong(CVV_FIELD)!!.toInt(),
                            document.getTimestamp(CADUCITY_FIELD)!!,
                            creditCardType,
                            document.getString(BANK_IBAN_FIELD)!!,
                        )
                    )
                }
                mutableData.value = listData
            }
            .addOnFailureListener { exception ->
                Log.w("HOOOL", "Error getting documents: ", exception)
            }

        return mutableData
    }

    fun deleteCreditCard(targetNumber: String) {
        firebase.db.collection(CREDIT_CARDS_COLLECTION)
            .document(targetNumber)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }
}