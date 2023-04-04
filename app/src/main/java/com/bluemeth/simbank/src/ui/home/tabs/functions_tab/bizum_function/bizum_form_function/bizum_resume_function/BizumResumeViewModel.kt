package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_resume_function

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.data.models.RequestedBizum
import com.bluemeth.simbank.src.domain.InsertMovementUseCase
import com.bluemeth.simbank.src.domain.SendRequestBizumUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BizumResumeViewModel @Inject constructor(
    private val insertMovementUseCase: InsertMovementUseCase,
    private val sendRequestBizumUseCase: SendRequestBizumUseCase,

    ) : ViewModel() {

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    fun makeBizum(
        iban: String,
        movement: Movement,
        beneficiaryMoney: Double,
        beneficiaryIban: String,
        notification: Notification
    ) {
        viewModelScope.launch {

            val bizumCreated =
                insertMovementUseCase(
                    iban,
                    movement,
                    beneficiaryMoney,
                    beneficiaryIban,
                    notification
                )

            if (bizumCreated) {
                _navigateToHome.value = Event(true)
            } else {
                _showErrorDialog.value = true
            }

        }
    }

    fun requestBizum(notification: Notification, requestedBizum: RequestedBizum) {
        viewModelScope.launch {

            val notificationInserted = sendRequestBizumUseCase(notification, requestedBizum)

            if (notificationInserted) {
                _navigateToHome.value = Event(true)
            } else {
                _showErrorDialog.value = true
            }

        }
    }
}