package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_manually

data class AddContactManuallyViewState(
    val isValidPhone: Boolean = true,
    val isValidPhoneConfirm: Boolean = true
) {
    fun isValidAddContact() = isValidPhone && isValidPhoneConfirm
}
