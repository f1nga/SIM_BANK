package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.model.model

data class TransferFormModel(
    val iban: String,
    val beneficiary: String,
    val import: String,
    val subject: String = ""
) {
    fun isNotEmpty() = iban.isNotEmpty() && beneficiary.isNotEmpty() && import.isNotEmpty()
}