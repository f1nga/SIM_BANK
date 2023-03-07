package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.transfer_function.resume_transfer_function

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.domain.InsertTransferUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResumeTransferViewModel @Inject constructor(private val insertTransferUseCase: InsertTransferUseCase) : ViewModel() {

    private var _movement : Movement? = null
    val movement: Movement?
        get() = _movement

    fun setTransfer(movement: Movement) {
        _movement = movement
    }

    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    fun insertTransferToDB(iban: String, movement: Movement) {
        viewModelScope.launch() {
            val transferInserted = insertTransferUseCase(iban, movement, 0.0, "dsaads")
            if(transferInserted) {
                _navigateToHome.value = Event(true)
            } else {
                _showErrorDialog.value = true
            }
        }
    }
}