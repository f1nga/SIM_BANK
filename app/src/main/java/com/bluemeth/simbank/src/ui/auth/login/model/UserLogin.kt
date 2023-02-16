package com.bluemeth.simbank.src.ui.auth.login.model

data class UserLogin(
    val email: String = "",
    val password: String = "",
    val showErrorDialog: Boolean = false
)