package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.core.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BizumViewModel @Inject constructor() : ViewModel(){
    private val _navigateToSendBizum = MutableLiveData<Event<Boolean>>()
    val navigateToSendBizum: LiveData<Event<Boolean>>
        get() = _navigateToSendBizum

    private val _navigateToRequestBizum = MutableLiveData<Event<Boolean>>()
    val navigateToRequestBizum: LiveData<Event<Boolean>>
        get() = _navigateToRequestBizum

    fun onSendBizumSelected() {
        _navigateToSendBizum.value = Event(true)
    }

    fun onRequestBizumSelected() {
        _navigateToRequestBizum.value = Event(true)
    }
}