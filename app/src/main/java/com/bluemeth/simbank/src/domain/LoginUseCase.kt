package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.network.AuthenticationService
import com.bluemeth.simbank.src.data.response.LoginResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authenticationService: AuthenticationService) {

    suspend operator fun invoke(email: String, password: String): LoginResult =
        authenticationService.login(email, password)
}