package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function

data class BizumFormViewState(
    val isValidImport: Boolean = true,
    val isValidSubject: Boolean = true,
    val isValidAddressesList: Boolean = true
) {
    fun isBizumFormValidated() = isValidImport && isValidSubject && isValidAddressesList
}
