package com.bluemeth.simbank.src.data.providers.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.providers.UserInitData
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject


class CreditCardRepository @Inject constructor(private val firebase: FirebaseClient){

    companion object {
        const val BANK_COLLECTION = "credit_cards"
        const val IBAN_FIELD = "bank_iban"
    }

    suspend fun createCreditCardTable() = runCatching {

        val creditCard = UserInitData.createCreditCard()

        firebase.db
            .collection(BANK_COLLECTION)
            .document(creditCard.number)
            .set(creditCard)
            .await()

    }.isSuccess

     fun getCreditCards() : LiveData<MutableList<CreditCard>> {
        val mutableData = MutableLiveData<MutableList<CreditCard>>()

        val bank_iban = "ES3371743112497932600524"

        firebase.db.collection(BANK_COLLECTION)
            .whereEqualTo(IBAN_FIELD, bank_iban)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<CreditCard>()
                for (document in documents) {
                    var number = document.getString("number").toString()
                    var money = document.get("money").toString()
                    var pin = document.get("pin").toString()
                    var cvv = document.get("cvv").toString()
                    val caducityTime = Timestamp(Date(Timestamp.now().toDate().year + 5, 2, 16))
                    var bank_iban = document.getString("bank_iban").toString()
                    val creditCard = CreditCard(number,money.toDouble(),pin.toInt(),cvv.toInt(),caducityTime,bank_iban)
                    listData.add(creditCard)
                }
                mutableData.value = listData
            }
            .addOnFailureListener { exception ->
                Log.w("HOOOL", "Error getting documents: ", exception)
            }

        return mutableData
    }
}