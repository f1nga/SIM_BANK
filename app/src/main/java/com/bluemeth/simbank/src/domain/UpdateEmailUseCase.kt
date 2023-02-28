package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.BankAccountRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import timber.log.Timber
import javax.inject.Inject

class UpdateEmailUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository,
    private val bankAccountRepository: BankAccountRepository,
) {

    suspend operator fun invoke(newEmail: String, iban: String, newUser: User) : Boolean{
        userRepository.updateUserEmail(authenticationRepository.getCurrentUser().email!!, newUser)

        val emailChanged = authenticationRepository.updateEmail(newEmail)

        return if(emailChanged) {
            bankAccountRepository.updateOwnerEmail(iban, newUser.email)
            prefs.saveEmail(newEmail)
            true
        } else {
            Timber.tag("ERROR").w("Email doesn't changed")
            false
        }
    }
}