package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.debit_card_form.model

class FormDebitCard(
    var alias : String,
    var pin : String
) {
    fun isNotEmpty() = alias.isNotEmpty() && pin.isNotEmpty()
}