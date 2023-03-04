package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.debit_card_form

class FormDebitCardViewState(
    val isValidAlias: Boolean = false,
    val isValidPin: Boolean = false
) {
    fun isFormDebitCardValidated() = isValidPin && isValidAlias
}