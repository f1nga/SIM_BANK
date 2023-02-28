package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.CreditCardRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import timber.log.Timber
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val authenticationService: AuthenticationRepository,
    private val userRepository: UserRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val creditCardRepository: CreditCardRepository
    ) {

    suspend operator fun invoke(email: String, iban: String): Boolean {
        val userDeleted = authenticationService.deleteAccount()

        return if(userDeleted) {
            userRepository.deleteUserByEmail(email)
            bankAccountRepository.deleteBankAccountByIban(iban)
            creditCardRepository.deleteCreditCardsByIban(iban)
            true
        } else {
            Timber.tag("Error").e("User not deleted")
            false
        }
    }
}