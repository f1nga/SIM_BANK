package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.model.ContactAgenda
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddContactFromAgendaViewModel @Inject constructor(
    val agendaRVAdapter: AgendaRVAdapter,
    private val userRepository: UserRepository
)  : ViewModel() {

    private val _navigateToBizumForm = MutableLiveData<Event<Boolean>>()
    val navigateToBizumForm: LiveData<Event<Boolean>>
        get() = _navigateToBizumForm

    fun getContactsFromDB(email: String): MutableLiveData<MutableList<ContactAgenda>> {
        val mutableData = MutableLiveData<MutableList<ContactAgenda>>()

        userRepository.getContactUsers(email).observeForever {
            it.add(ContactAgenda("Jaume Pescador", 342432243))
            it.add(ContactAgenda("Pere Cullera", 342432243))
            it.add(ContactAgenda("Radamel Pinetell", 342432243))
            it.add(ContactAgenda("Pep Gamarus", 342432243))
            mutableData.value = it
        }

        return mutableData
    }

    fun onAcceptSelected() {
        _navigateToBizumForm.value = Event(true)
    }
}