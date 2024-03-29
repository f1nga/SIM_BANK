package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function.resume_transfer_function

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.domain.InsertMovementUseCase
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.transfer_form_function.models.TransferFormModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResumeTransferViewModel @Inject constructor(
    private val insertMovementUseCase: InsertMovementUseCase,
) : ViewModel() {

    private var _reUseTransferArguments: TransferFormModel? = null
    var reUseTransferArguments: TransferFormModel? = null
        get() = _reUseTransferArguments

    private var _movement: Movement? = null
    var movement: Movement? = null
        get() = _movement

    fun setTransfer(movement: Movement) {
        _movement = movement
    }

    fun setTransferFormModel(transferFormModel: TransferFormModel) {
        _reUseTransferArguments = transferFormModel
    }

    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    fun insertTransferToDB(
        iban: String,
        movement: Movement,
        beneficiaryMoney: Double,
        beneficiaryIban: String,
        notification: Notification
    ) {
        viewModelScope.launch() {
            val transferInserted = insertMovementUseCase(
                iban,
                movement,
                beneficiaryMoney,
                beneficiaryIban,
                notification
            )
            if (transferInserted) {
                _navigateToHome.value = true
            } else {
                _showErrorDialog.value = true
            }
        }
    }
}