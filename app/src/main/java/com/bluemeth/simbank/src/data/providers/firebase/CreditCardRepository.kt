package com.bluemeth.simbank.src.data.providers.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.providers.UserInitData
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
    }

    suspend fun createCreditCardTable() = runCatching {

        val creditCard = UserInitData.createCreditCard()

        firebase.db
            .collection(CREDIT_CARDS_COLLECTION)
            .document(creditCard.number)
            .set(creditCard)
            .await()

    }.isSuccess

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
                    listData.add(
                        CreditCard(
                            document.getString(NUMBER_FIELD)!!,
                            document.getDouble(MONEY_FIELD)!!,
                            document.getLong(PIN_FIELD)!!.toInt(),
                            document.getLong(CVV_FIELD)!!.toInt(),
                            document.getTimestamp(CADUCITY_FIELD)!!,
                            document.getString(BANK_IBAN_FIELD)!!
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
}