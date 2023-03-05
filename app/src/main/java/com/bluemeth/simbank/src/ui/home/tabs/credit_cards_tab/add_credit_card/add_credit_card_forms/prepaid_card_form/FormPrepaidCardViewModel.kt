package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.prepaid_card_form

import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.prepaid_card_form.model.FormPrepaidCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class FormPrepaidCardViewModel @Inject constructor() : ViewModel()  {
    private companion object {
        const val FIELD_LENGTH = 4
        const val MINIM_LENGTH = 1
    }

    private val _viewState = MutableStateFlow(FormPrepaidCardViewState())
    val viewState: StateFlow<FormPrepaidCardViewState>
        get() = _viewState

    fun onFieldsChanged(formPrepaidCard: FormPrepaidCard) {
        _viewState.value = formPrepaidCard.toFormPrepaidCardModelState()
    }

    fun isValidAlias(alias: String) = alias.length >= FIELD_LENGTH
    fun isValidPin(pin: String) = pin.length == FIELD_LENGTH
    fun isValidMoney(money: String) = money.length in MINIM_LENGTH..FIELD_LENGTH

    fun FormPrepaidCard.toFormPrepaidCardModelState(): FormPrepaidCardViewState {
        return FormPrepaidCardViewState(
            isValidAlias = isValidAlias(alias),
            isValidPin = isValidPin(pin),
            isValidMoney = isValidMoney(money)
        )
    }
}