package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_resume_function

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.domain.InsertTransferUseCase
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.AgendaRVAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BizumResumeViewModel @Inject constructor(
    val agendaRVAdapter: AgendaRVAdapter,
    private val insertTransferUseCase: InsertTransferUseCase,
) : ViewModel() {

    private val _navigateToVerifyEmail = MutableLiveData<Event<Boolean>>()
    val navigateToVerifyEmail: LiveData<Event<Boolean>>
        get() = _navigateToVerifyEmail

    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    fun makeBizum(iban: String, movement: Movement, beneficiaryMoney: Double, beneficiaryIban: String) {
        viewModelScope.launch {

            val bizumCreated = insertTransferUseCase(iban, movement, beneficiaryMoney, beneficiaryIban)

            if(bizumCreated) {
                _navigateToVerifyEmail.value = Event(true)
            }
            else {
                _showErrorDialog.value = true
            }

        }
    }

}