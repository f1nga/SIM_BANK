package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_manually

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.core.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddContactManuallyViewModel @Inject constructor() : ViewModel(){

    private val _navigateToBizumForm = MutableLiveData<Event<Boolean>>()
    val navigateToBizumForm: LiveData<Event<Boolean>>
        get() = _navigateToBizumForm


    fun onAddContactSelected(phone: String) {
        _navigateToBizumForm.value = Event(true)
    }

}