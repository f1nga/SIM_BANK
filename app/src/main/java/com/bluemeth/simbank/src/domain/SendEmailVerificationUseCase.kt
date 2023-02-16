package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.network.AuthenticationService
import javax.inject.Inject

class SendEmailVerificationUseCase @Inject constructor(private val authenticationService: AuthenticationService) {

    suspend operator fun invoke() = authenticationService.sendVerificationEmail()
}