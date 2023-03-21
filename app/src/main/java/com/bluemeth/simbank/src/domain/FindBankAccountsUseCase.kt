package com.bluemeth.simbank.src.domain

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import javax.inject.Inject

class FindBankAccountsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val bankAccountRepository: BankAccountRepository
) {

    suspend operator fun invoke(
        name: String,
    ): MutableLiveData<BankAccount> {

        val bank = MutableLiveData<BankAccount>()

        bankAccountRepository.findBankAccountByEmail(name).observeForever {
            Log.i("hool120", it.toString())
            bank.value = it

        }

        return bank
    }
}
