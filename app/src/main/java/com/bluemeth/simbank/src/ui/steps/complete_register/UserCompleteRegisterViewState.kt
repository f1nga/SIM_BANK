package com.bluemeth.simbank.src.ui.steps.complete_register

data class UserCompleteRegisterViewState(
    val isValidName: Boolean = true,
    val isValidLastName: Boolean = true,
    val isValidSecondName: Boolean = true,
    val isValidPhoneNumber: Boolean = true

) {
    fun isValidUser() = isValidName && isValidLastName && isValidSecondName && isValidPhoneNumber
}

