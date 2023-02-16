package com.bluemeth.simbank.src.ui.auth.signin.model

data class UserSignIn(
    val nickName: String,
    val email: String,
    val password: String,
    val passwordConfirmation: String,
    val phoneNumber: Int
) {
    fun isNotEmpty() =
        nickName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && passwordConfirmation.isNotEmpty()
}

fun Int.length() = when(this) {
    0 -> 1
    else -> kotlin.math.log10(kotlin.math.abs(toDouble())).toInt() + 1
}