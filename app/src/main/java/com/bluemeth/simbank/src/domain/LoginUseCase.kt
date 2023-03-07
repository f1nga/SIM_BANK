package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.response.LoginResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authenticationRepository: AuthenticationRepository) {

    suspend operator fun invoke(email: String, password: String): LoginResult =
        authenticationRepository.login(email, password)
}