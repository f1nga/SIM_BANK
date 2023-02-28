package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_password

data class UpdatePasswordViewState(
    val isValidPassword: Boolean = true,
    val isValidPasswordConfirmation: Boolean = true,

) {
    fun passwordValidated() = isValidPassword && isValidPasswordConfirmation
}
