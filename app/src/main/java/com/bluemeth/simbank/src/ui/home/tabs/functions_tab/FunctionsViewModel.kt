package com.bluemeth.simbank.src.ui.home.tabs.functions_tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.core.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FunctionsViewModel @Inject constructor() : ViewModel() {
    private val _navigateToTransferFunction = MutableLiveData<Event<Boolean>>()
    val navigateToTransferFunction: LiveData<Event<Boolean>>
        get() = _navigateToTransferFunction

    private val _navigateToBizumFunction = MutableLiveData<Event<Boolean>>()
    val navigateToBizumFunction: LiveData<Event<Boolean>>
        get() = _navigateToBizumFunction

    fun onTransferFunctionSelected() {
        _navigateToTransferFunction.value = Event(true)
    }

    fun onBizumFunctionSelected() {
        _navigateToBizumFunction.value = Event(true)
    }
}