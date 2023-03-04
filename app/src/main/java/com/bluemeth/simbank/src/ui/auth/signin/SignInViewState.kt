package com.bluemeth.simbank.src.ui.auth.signin

data class SignInViewState(
    val isLoading: Boolean = false,
    val isValidEmail: Boolean = true,
    val isValidPassword: Boolean = true,
    val isValidNickName: Boolean = true,
    val isValidPhoneNumber: Boolean = true
) {
    fun userValidated() = isValidEmail && isValidPassword && isValidNickName && isValidPhoneNumber
}