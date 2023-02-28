package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields.states

data class UpdatePhoneViewState(
    val isValidPhoneNumber: Boolean = true
) {
    fun phoneValidated() = isValidPhoneNumber
}
