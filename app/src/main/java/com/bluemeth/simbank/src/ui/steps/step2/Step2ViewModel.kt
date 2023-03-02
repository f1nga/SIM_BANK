package com.bluemeth.simbank.src.ui.steps.step2

import android.view.View
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.ui.steps.step3.Step3ViewModel
import com.bluemeth.simbank.src.ui.steps.step3.Step3ViewState
import com.bluemeth.simbank.src.ui.steps.step3.model.Step3Model
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

    var isGeneratedIbanButtonClicked : Boolean = false
    var isGeneratedMoneyButtonClicked : Boolean = false

    fun isValidGeneratedIbanButtonClicked(alias: String){
        isGeneratedIbanButtonClicked = true
        onFieldsChanged(alias)
    }
    fun isValidGeneratedMoneyButtonClicked(alias: String){
        isGeneratedMoneyButtonClicked = true
        onFieldsChanged(alias)
    }

    fun onFieldsChanged(alias : String) {
        _viewState.value = toStep2ModelState(alias)
    }
    fun isValidAlias(alias: String) = alias.length >= FIELD_LENGTH
    fun toStep2ModelState(alias: String):Step2ViewState{
        return Step2ViewState(
            isButtonIbanClicked =  isGeneratedIbanButtonClicked,
            isButtonMoneyClicked = isGeneratedMoneyButtonClicked,
            isValidAlias = isValidAlias(alias)
        )
    }


}