package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.prepaid_card_form.model

class FormPrepaidCard(
    var alias : String,
    var pin : String,
    var money : String
) {
    fun isNotEmpty() = alias.isNotEmpty() && pin.isNotEmpty() && money.isNotEmpty()
}