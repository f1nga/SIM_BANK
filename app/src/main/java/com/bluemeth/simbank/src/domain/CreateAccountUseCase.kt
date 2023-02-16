package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.CreditCardRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val authenticationService: AuthenticationRepository,
    private val userRepository: UserRepository,
    private val creditCardRepository: CreditCardRepository,
    private val bankAccountRepository: BankAccountRepository

) {

    suspend operator fun invoke(userSignIn: UserSignIn): Boolean {
        val accountCreated =
            authenticationService.createAccount(userSignIn.email, userSignIn.password) != null
        return if (accountCreated) {
            true
            userRepository.createUserTable(userSignIn)
            bankAccountRepository.createBankAccountTable()
            creditCardRepository.createCreditCardTable()
        } else {
            false
        }
    }
}