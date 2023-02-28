package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(private val authenticationService: AuthenticationRepository) {

    suspend operator fun invoke(email: String) = authenticationService.forgotPassword(email)
}