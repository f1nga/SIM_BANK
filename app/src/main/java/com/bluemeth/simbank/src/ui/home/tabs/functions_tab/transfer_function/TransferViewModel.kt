package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.model.TransferFormModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor() : ViewModel() {
    private companion object {
        const val MIN_BENEFICIARY_LENGTH = 6
        const val IBAN_LENGTH = 24
    }

    private val _viewState = MutableStateFlow(TransferFormViewState())
    val viewState: StateFlow<TransferFormViewState>
        get() = _viewState

    private val _navigateToTransferResum = MutableLiveData<Event<Boolean>>()
    val navigateToTransferResum: LiveData<Event<Boolean>>
        get() = _navigateToTransferResum

    fun onContinueSelected(transferFormModel: TransferFormModel) {
        val viewState = transferFormModel.toUpdateViewState()

        if (viewState.isTransferValidated() && transferFormModel.isNotEmpty()) {
            _navigateToTransferResum.value = Event(true)
        } else {
            onNameFieldsChanged(transferFormModel)
        }
    }

    fun onNameFieldsChanged(transferFormModel: TransferFormModel) {
        _viewState.value = transferFormModel.toUpdateViewState()
    }

    private fun isValidImport(import: String): Boolean {
        if (import.isNotEmpty()) {
            return import.toDouble() in 0.01..10000.0
        }
        return import.isEmpty()
    }

    private fun isValidBeneficiary(beneficiary: String) =
        beneficiary.length >= MIN_BENEFICIARY_LENGTH || beneficiary.isEmpty()

    private fun isValidIbanSpaces(iban: String): Boolean {
        return iban.split(" ").size == 1

    }

    private fun isValidIban(iban: String): Boolean {
        if (iban.isNotEmpty()) {
            if (iban[0].isDigit()) return false
            if (iban.length > 1 && iban[1].isDigit()) return false

            if (iban.length > 2) {
                for (i in 2 until iban.length) {
                    if (!iban[i].isDigit()) return false
                }
            }
        }

        return iban.length == IBAN_LENGTH || iban.isEmpty()
    }


    private fun TransferFormModel.toUpdateViewState(): TransferFormViewState {
        return TransferFormViewState(
            isValidIbanSpaces = isValidIbanSpaces(iban),
            isValidIban = isValidIban(iban),
            isValidBeneficiary = isValidBeneficiary(beneficiary),
            isValidImport = isValidImport(import),
            )
    }
}