package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.BizumFormModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.ContactBizum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BizumFormViewModel @Inject constructor(
    val addressesRVAdapter: AddressesRVAdapter
) : ViewModel() {

    private companion object {
        const val MAX_BIZUM_IMPORT = 500.0
        const val MIN_BIZUM_IMPORT = 0.5
        const val SUBJECT_MAX_LENGTH = 35
    }

    private val _viewState = MutableStateFlow(BizumFormViewState())
    val viewState: StateFlow<BizumFormViewState>
        get() = _viewState

    private val _navigateToBizumResum = MutableLiveData<Event<Boolean>>()
    val navigateToBizumResum: LiveData<Event<Boolean>>
        get() = _navigateToBizumResum

    private var _bizumFormArguments: BizumFormModel? = null
    val bizumFormArguments: BizumFormModel?
        get() = _bizumFormArguments

    private var _bizumFormModel : BizumFormModel? = null
    val bizumFormModel: BizumFormModel?
        get() = _bizumFormModel

    fun setBizumFormModel(bizumFormModel: BizumFormModel) {
        _bizumFormModel = bizumFormModel
    }

    fun setBizumFormArguments(bizumFormModel: BizumFormModel) {
        _bizumFormArguments = bizumFormModel
    }

    fun onContinueSelected(bizumFormModel: BizumFormModel) {
        val viewState = bizumFormModel.toUpdateViewState()

        if (viewState.isBizumFormValidated() && bizumFormModel.isNotEmpty()) {
            _navigateToBizumResum.value = Event(true)
        } else {
            onNameFieldsChanged(bizumFormModel)
        }
    }

    fun onNameFieldsChanged(bizumFormModel: BizumFormModel) {
        _viewState.value = bizumFormModel.toUpdateViewState()
    }

    private fun isValidImport(import: String): Boolean {
        if (import.isNotEmpty()) {
            return import.toDouble() in MIN_BIZUM_IMPORT..MAX_BIZUM_IMPORT
        }
        return false
    }

    private fun isValidSubject(subject: String) =
        subject.length <= SUBJECT_MAX_LENGTH || subject.isEmpty()

    private fun isValidAddressesList(addressesList: MutableList<ContactBizum>): Boolean {
        return addressesList.isNotEmpty()
    }

    private fun BizumFormModel.toUpdateViewState(): BizumFormViewState {
        return BizumFormViewState(
            isValidImport = isValidImport(import),
            isValidSubject = isValidSubject(subject),
            isValidAddressesList = isValidAddressesList(addressesList!!),
        )
    }
}