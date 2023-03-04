package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.debit_card_form

import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.debit_card_form.model.FormDebitCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class FormDebitCardViewModel @Inject constructor() : ViewModel()  {
    private companion object {
        const val FIELD_LENGTH = 4
    }

    private val _viewState = MutableStateFlow(FormDebitCardViewState())
    val viewState: StateFlow<FormDebitCardViewState>
        get() = _viewState

    fun onFieldsChanged(formDebitCard: FormDebitCard) {
        _viewState.value = formDebitCard.toFormDebitCardModelState()
    }

    fun isValidAlias(alias: String) = alias.length >= FIELD_LENGTH
    fun isValidPin(alias: String) = alias.length == FIELD_LENGTH

    fun FormDebitCard.toFormDebitCardModelState(): FormDebitCardViewState {
        return FormDebitCardViewState(
            isValidAlias = isValidAlias(alias),
            isValidPin = isValidPin(pin)
        )
    }
}