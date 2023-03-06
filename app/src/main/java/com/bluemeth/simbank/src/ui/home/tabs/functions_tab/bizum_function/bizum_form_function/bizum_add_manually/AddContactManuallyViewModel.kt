package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_manually

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_manually.model.ContactManually
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AddContactManuallyViewModel @Inject constructor() : ViewModel(){

    private companion object {
        const val PHONE_LENGTH = 9
    }

    private val _navigateToBizumForm = MutableLiveData<Event<Boolean>>()
    val navigateToBizumForm: LiveData<Event<Boolean>>
        get() = _navigateToBizumForm

    private val _viewState = MutableStateFlow(AddContactManuallyViewState())
    val viewState: StateFlow<AddContactManuallyViewState>
        get() = _viewState

    fun onAddContactSelected(contactManually: ContactManually) {
        val viewState = contactManually.toUpdateViewState()

        if (viewState.isValidAddContact() && contactManually.isNotEmpty()) {
            _navigateToBizumForm.value = Event(true)
        } else {
            onNameFieldsChanged(contactManually)
        }
    }

    fun onNameFieldsChanged(contactManually: ContactManually) {
        _viewState.value = contactManually.toUpdateViewState()
    }

    private fun isValidPhone(phone: String) = phone.length == PHONE_LENGTH || phone.isEmpty()

    private fun isValidPhoneConfirm(phone: String, phoneConfirm: String) = phoneConfirm == phone || phoneConfirm.isEmpty()

    private fun ContactManually.toUpdateViewState(): AddContactManuallyViewState {
        return AddContactManuallyViewState(
            isValidPhone = isValidPhone(phoneNumber),
            isValidPhoneConfirm = isValidPhoneConfirm(phoneNumber, phoneNumberConfirm)
        )
    }

}