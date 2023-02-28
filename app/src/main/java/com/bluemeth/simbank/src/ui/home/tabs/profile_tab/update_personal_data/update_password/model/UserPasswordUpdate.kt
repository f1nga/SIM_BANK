package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_password.model

data class UserPasswordUpdate(
    val password: String,
    val passwordConfirm: String
) {
    fun isNotEmpty() =
        password.isNotEmpty() && passwordConfirm.isNotEmpty()
}
