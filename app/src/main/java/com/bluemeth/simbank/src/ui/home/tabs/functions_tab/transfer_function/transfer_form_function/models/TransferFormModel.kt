package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function.models

data class TransferFormModel(
    val iban: String,
    val beneficiary: String,
    val import: String,
    val subject: String = ""
) {
    fun isNotEmpty() = iban.isNotEmpty() && beneficiary.isNotEmpty() && import.isNotEmpty()
}