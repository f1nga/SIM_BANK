package com.bluemeth.simbank.src.data.providers.firebase

import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.providers.UserInitData
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BankAccountRepository @Inject constructor(private val firebase: FirebaseClient) {

    companion object {
        const val BANK_COLLECTION = "bank_accounts"
    }

    suspend fun createBankAccountTable() = runCatching {
        val bankAccount = UserInitData.createBankAccount()

        firebase.db
            .collection(BANK_COLLECTION)
            .document(bankAccount.iban)
            .set(bankAccount)
            .await()

    }.isSuccess
}