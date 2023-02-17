package com.bluemeth.simbank.src.data.providers.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.CreditCard
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class CardRepository {
    val mutableData = MutableLiveData<MutableList<CreditCard>>()

    fun getCardData():LiveData<MutableList<CreditCard>>{
        query()
        return mutableData
    }

    fun query(){

        val db = Firebase.firestore
        val bank_iban = "ES3371743112497932600524"
        db.collection("credit_cards")
            .whereEqualTo("bank_iban", bank_iban)
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
    }
}