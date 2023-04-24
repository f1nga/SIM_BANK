package com.bluemeth.simbank.src.ui.auth.signin.model

data class UserSignIn(
    val nickName: String,
    val email: String,
    val password: String,
    val passwordConfirmation: String,
    val phoneNumber: String,
) {
    fun isNotEmpty() =
        nickName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && passwordConfirmation.isNotEmpty()
}