package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields.states

data class UpdatePasswordViewState(
    val isValidPasswordLength: Boolean = true,
    val isValidPassword: Boolean = true,
    val isValidPasswordConfirmation: Boolean = true,
    val isValidPasswordsCoincide: Boolean = true

) {
    fun passwordValidated() = isValidPasswordLength && isValidPassword && isValidPasswordConfirmation
    fun passwordsCoincide() = isValidPasswordsCoincide
}
