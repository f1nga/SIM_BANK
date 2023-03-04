package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.models.BankAccount
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.CreditCardRepository
import timber.log.Timber
import javax.inject.Inject

class CreateUserDataUseCase @Inject constructor(
    private val creditCardRepository: CreditCardRepository,
    private val bankAccountRepository: BankAccountRepository

) {
    suspend operator fun invoke(bankAccount: BankAccount, creditCard: CreditCard): Boolean {
        val bankAccoundCreated = bankAccountRepository.insertBankAccount(bankAccount)

        return if(bankAccoundCreated) {
            creditCardRepository.insertCreditCard(creditCard)
        } else {
            Timber.tag("ERROR").w("Cannot create bank account")
            false
        }
    }
}

