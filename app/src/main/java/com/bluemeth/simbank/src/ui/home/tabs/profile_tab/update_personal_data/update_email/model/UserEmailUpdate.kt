package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_email.model

data class UserEmailUpdate(
     val email: String,
     val emailConfirm: String
) {
    fun isNotEmpty() =
        email.isNotEmpty() && emailConfirm.isNotEmpty()
}
