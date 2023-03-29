package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.providers.firebase.*
import timber.log.Timber
import javax.inject.Inject

class UpdateNameUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository,
    private val movementRepository: MovementRepository,
) {

    suspend operator fun invoke(fullName: String, oldName: String) : Boolean{
        val nameChanged = userRepository.updateUserName(authenticationRepository.getCurrentUser().email!!, fullName)

        return if(nameChanged) {
            movementRepository.updateBeneficiaryName(oldName, fullName)
            true
        } else {
            Timber.tag("ERROR").w("Email doesn't changed")
            false
        }
    }
}