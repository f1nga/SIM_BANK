package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function

data class TransferFormViewState(
    val isValidIbanSpaces: Boolean = true,
    val isValidIban : Boolean = true,
    val isValidBeneficiary: Boolean = true,
    val isValidImport: Boolean = true,

){
    fun isTransferValidated() = isValidIban && isValidBeneficiary && isValidImport
}