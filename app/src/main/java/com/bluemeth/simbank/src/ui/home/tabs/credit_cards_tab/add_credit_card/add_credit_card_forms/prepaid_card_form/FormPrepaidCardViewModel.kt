package com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.prepaid_card_form

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.utils.CreditCardType
import com.bluemeth.simbank.src.domain.InsertCreditCardUseCase
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.add_credit_card_forms.prepaid_card_form.model.FormPrepaidCard
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
@HiltViewModel
class FormPrepaidCardViewModel @Inject constructor(
    private val insertCreditCardUseCase: InsertCreditCardUseCase
) : ViewModel()  {
    private companion object {
        const val FIELD_LENGTH = 4
        const val MINIM_LENGTH = 1
    }

    private val _viewState = MutableStateFlow(FormPrepaidCardViewState())
    val viewState: StateFlow<FormPrepaidCardViewState>
        get() = _viewState
    private val _navigateToCards = MutableLiveData<Event<Boolean>>()
    val navigateToCards: LiveData<Event<Boolean>>
        get() = _navigateToCards
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

    fun onFinishSelected(context: Context, iban: String, formPrepaidCard: FormPrepaidCard) {
        val viewState = formPrepaidCard.toFormPrepaidCardModelState()
        if (viewState.isFormPrepaidCardValidated() && formPrepaidCard.isNotEmpty()) {
            var debitCardNumber = ""

            for (i in 1..16) {
                debitCardNumber += (0..9).random()
            }
            val alias = formPrepaidCard.alias
            val money = (1000..3000).random().toDouble()
            val pin = formPrepaidCard.pin.toInt()
            val cvv = (100..999).random()
            val caducityTime = Timestamp(Date(Timestamp.now().toDate().year + 5, 2, 16))

            val creditCard = CreditCard(
                debitCardNumber,
                alias,
                money,
                pin,
                cvv,
                caducityTime,
                CreditCardType.Prepago,
                iban
            )
            viewModelScope.launch {
                val cardInserted = insertCreditCardUseCase(creditCard)
                if (cardInserted) {
                    _navigateToCards.value = Event(true)
                }
            }
        } else {
            onFieldsChanged(formPrepaidCard)
            Toast.makeText(context, "¡Porfavor, completa los campos antes de solicitar la nueva tarjeta!", Toast.LENGTH_SHORT).show()
        }
    }
}