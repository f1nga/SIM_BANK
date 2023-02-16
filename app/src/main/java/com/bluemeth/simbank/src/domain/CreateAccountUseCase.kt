package com.bluemeth.simbank.src.domain

import com.bluemeth.simbank.src.data.network.AuthenticationService
import com.bluemeth.simbank.src.data.network.UserService
import com.bluemeth.simbank.src.ui.auth.signin.model.UserSignIn
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) {

    suspend operator fun invoke(userSignIn: UserSignIn): Boolean {
        val accountCreated =
            authenticationService.createAccount(userSignIn.email, userSignIn.password) != null
        return if (accountCreated) {
            userService.createUserTable(userSignIn)
        } else {
            false
        }
    }
}