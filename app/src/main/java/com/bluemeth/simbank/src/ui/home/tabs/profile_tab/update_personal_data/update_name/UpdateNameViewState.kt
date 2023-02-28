package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_personal_data.update_name

class UpdateNameViewState (
    val isValidName: Boolean = true,
    val isValidLastName: Boolean = true,
    val isValidSecondName: Boolean = true

) {
    fun fullNameValidated() = isValidName && isValidLastName && isValidSecondName
}
