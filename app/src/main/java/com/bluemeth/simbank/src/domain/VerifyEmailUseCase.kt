package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(private val authenticationService: AuthenticationRepository) {

    operator fun invoke(): Flow<Boolean> = authenticationService.verifiedAccount

}