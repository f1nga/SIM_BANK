package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_phone

data class UpdatePhoneViewState(
    val isValidPhoneNumber: Boolean = true
) {
    fun phoneValidated() = isValidPhoneNumber
}
