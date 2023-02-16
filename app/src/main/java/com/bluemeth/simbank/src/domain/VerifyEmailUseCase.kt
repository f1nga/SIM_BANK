package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.network.AuthenticationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(private val authenticationService: AuthenticationService) {

    operator fun invoke(): Flow<Boolean> = authenticationService.verifiedAccount

}