package com.bluemeth.simbank.src.data.providers.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.providers.UserInitData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BankAccountRepository @Inject constructor(private val firebase: FirebaseClient) {

    companion object {
        const val BANK_COLLECTION = "bank_accounts"
        const val USER_EMAIL_FIELD = "user_email"
        const val IBAN_FIELD = "iban"
        const val MONEY_FIELD = "money"
    }

    suspend fun createBankAccountTable() = runCatching {
        val bankAccount = UserInitData.createBankAccount()

        firebase.db
            .collection(BANK_COLLECTION)
            .document(bankAccount.iban)
            .set(bankAccount)
            .await()

    }.isSuccess

    fun findBankAccountByEmail(email: String): MutableLiveData<BankAccount> {
        val bankAccount = MutableLiveData<BankAccount>()

        firebase.db.collection(BANK_COLLECTION)
            .whereEqualTo(USER_EMAIL_FIELD, email)
            .get()
            .addOnSuccessListener { documents ->
                bankAccount.value = BankAccount(
                    documents.first().getString(IBAN_FIELD)!!,
                    documents.first().getDouble(MONEY_FIELD)!!,
                    documents.first().getString(USER_EMAIL_FIELD)!!,
                )
            }
            .addOnFailureListener { exception ->
                Log.w("HOOOL", "Error getting documents: ", exception)
            }

        return bankAccount
    }

    fun updateOwnerEmail(iban: String, newEmail: String) {
        firebase.db
            .collection(BANK_COLLECTION)
            .document(iban)
            .update(USER_EMAIL_FIELD, newEmail)
    }
}