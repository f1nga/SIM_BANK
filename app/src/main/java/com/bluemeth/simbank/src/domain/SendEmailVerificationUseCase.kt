package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import javax.inject.Inject

class SendEmailVerificationUseCase @Inject constructor(private val authenticationService: AuthenticationRepository) {

    suspend operator fun invoke() = authenticationService.sendVerificationEmail()
}