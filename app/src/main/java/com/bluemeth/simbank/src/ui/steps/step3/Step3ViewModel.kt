package com.bluemeth.simbank.src.ui.steps.step3

import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.ui.steps.step3.model.Step3Model
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class Step3ViewModel @Inject constructor(): ViewModel(){

    private companion object{
        const val FIELD_LENGTH = 4
    }

    private val _viewState = MutableStateFlow(Step3ViewState())
    val viewState: StateFlow<Step3ViewState>
        get() = _viewState

     var isGeneratedButtonClicked : Boolean = false

    fun onGeneratedButtonClicked(step3Model: Step3Model) {
        isGeneratedButtonClicked = true
        _viewState.value = step3Model.toStep3ModelState()
    }

    fun onFieldsChanged(step3Model: Step3Model) {
        _viewState.value = step3Model.toStep3ModelState()
    }

    fun isValidAlias(alias: String) = alias.length >= FIELD_LENGTH

    fun isValidPin(pin: String) = pin.length == FIELD_LENGTH

    fun isValidGeneratedButtonClicked() = isGeneratedButtonClicked

    fun Step3Model.toStep3ModelState(): Step3ViewState {
        return Step3ViewState(
            isButtonClicked = isValidGeneratedButtonClicked(),
            isValidAlias = isValidAlias(alias),
            isValidPin = isValidPin(pin),
        )
    }
}