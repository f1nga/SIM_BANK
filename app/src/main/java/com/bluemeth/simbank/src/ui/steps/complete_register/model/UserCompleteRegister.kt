package com.bluemeth.simbank.src.ui.steps.complete_register.model

data class UserCompleteRegister(
    val name: String,
    val lastName: String,
    val secondName: String,
    val phoneNumber: String
) {
    fun isNotEmpty() =
        name.isNotEmpty() && lastName.isNotEmpty() && secondName.isNotEmpty() && phoneNumber.isNotEmpty()
}
