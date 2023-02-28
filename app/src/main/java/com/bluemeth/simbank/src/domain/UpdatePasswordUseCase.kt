package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.SimBankApp.Companion.prefs
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import timber.log.Timber
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val authenticationService: AuthenticationRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(email: String, password: String): Boolean {
        val passwordUpdate = authenticationService.updatePassword(password)

        return if(passwordUpdate) {
            userRepository.updateUserPassword(email, password)
            prefs.savePassword(password)
            true
        } else {
            Timber.tag("ERROR").w("Logout doesn't complete succesfully")
            false
        }
    }
}