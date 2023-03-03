package com.bluemeth.simbank.src.ui.steps.step2

import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.ui.steps.step2.model.Step2Model
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class Step2ViewModel @Inject constructor(): ViewModel(){

    private companion object{
        const val FIELD_LENGTH = 4
    }

    private val _viewState = MutableStateFlow(Step2ViewState())
    val viewState: StateFlow<Step2ViewState>
        get() = _viewState

    fun onFieldsChanged(step2Model: Step2Model) {
        _viewState.value = step2Model.toStep2ModelState()
    }
    fun isValidAlias(alias: String) = alias.length >= FIELD_LENGTH

    fun Step2Model.toStep2ModelState(): Step2ViewState{
        return Step2ViewState(
            isButtonIbanClicked =  isGeneratedIBANButtonClicked,
            isButtonMoneyClicked = isGeneratedMoneyButtonClicked,
            isValidAlias = isValidAlias(alias)
        )
    }


}