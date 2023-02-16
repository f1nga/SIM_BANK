package com.bluemeth.simbank.src.data.providers.firebase

import com.bluemeth.simbank.src.data.providers.UserInitData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CreditCardRepository @Inject constructor(private val firebase: FirebaseClient){

    companion object {
        const val BANK_COLLECTION = "credit_cards"
    }

    suspend fun createCreditCardTable() = runCatching {
        val creditCard = UserInitData.createCreditCard()

        firebase.db
            .collection(BANK_COLLECTION)
            .document(creditCard.number)
            .set(creditCard)
            .await()

    }.isSuccess
}