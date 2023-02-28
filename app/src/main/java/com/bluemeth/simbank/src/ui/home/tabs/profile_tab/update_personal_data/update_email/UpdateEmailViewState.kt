package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_email

data class UpdateEmailViewState(
    val isValidEmail: Boolean = true,
    val isValidEmailConfirm: Boolean = true

) {
    fun emailValidated() = isValidEmail && isValidEmailConfirm
}
