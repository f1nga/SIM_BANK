package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_manually.model

data class ContactManually(
    val phoneNumber: String,
    val phoneNumberConfirm: String
) {
    fun isNotEmpty() = phoneNumber.isNotEmpty() && phoneNumberConfirm.isNotEmpty()
}
