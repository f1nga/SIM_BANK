package com.bluemeth.simbank.src.data.providers.firebase

import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.utils.Methods
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class BankAccountRepository @Inject constructor(private val firebase: FirebaseClient) {

    companion object {
        const val BANK_COLLECTION = "bank_accounts"
        const val USER_EMAIL_FIELD = "user_email"
        const val IBAN_FIELD = "iban"
        const val MONEY_FIELD = "money"
    }

    suspend fun insertBankAccount(bankAccount: BankAccount) = runCatching {

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
                Timber.tag("Error").w(exception, "Error getting documents: ")
            }

        return bankAccount
    }

    fun findBankAccountByIban(iban: String): MutableLiveData<BankAccount> {
        val bankAccount = MutableLiveData<BankAccount>()

        firebase.db.collection(BANK_COLLECTION)
            .whereEqualTo(IBAN_FIELD, iban)
            .get()
            .addOnSuccessListener { documents ->
                bankAccount.value = BankAccount(
                    documents.first().getString(IBAN_FIELD)!!,
                    documents.first().getDouble(MONEY_FIELD)!!,
                    documents.first().getString(USER_EMAIL_FIELD)!!,
                )
            }
            .addOnFailureListener { exception ->
                Timber.tag("Error").w(exception, "Error getting documents: ")
            }

        return bankAccount
    }

    fun updateOwnerEmail(iban: String, newEmail: String) {
        firebase.db
            .collection(BANK_COLLECTION)
            .document(iban)
            .update(USER_EMAIL_FIELD, newEmail)
    }

    fun deleteBankAccountByIban(iban: String) {
        firebase.db
            .collection(BANK_COLLECTION)
            .document(iban)
            .delete()
    }

    suspend fun makeMovement(iban: String, remainingMoney: Double) = runCatching {
        firebase.db
            .collection(BANK_COLLECTION)
            .document(iban)
            .update(MONEY_FIELD, remainingMoney)
            .await()
    }.isSuccess

    suspend fun movementReceived( beneficiaryIban: String, beneficiaryMoney: Double, movementImport: Double) = runCatching {

        firebase.db
            .collection(BANK_COLLECTION)
            .document(beneficiaryIban)
            .update(MONEY_FIELD, Methods.roundOffDecimal(beneficiaryMoney + movementImport))
            .await()
    }.isSuccess
}