package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.model.ContactAgenda
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddContactFromAgendaViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _navigateToBizumForm = MutableLiveData<Event<Boolean>>()
    val navigateToBizumForm: LiveData<Event<Boolean>>
        get() = _navigateToBizumForm

    fun getUserContactsFromDB(email: String): MutableLiveData<MutableList<ContactAgenda>> {
        val mutableData = MutableLiveData<MutableList<ContactAgenda>>()

        viewModelScope.launch {
            userRepository.getUsers(email).observeForever { listContacts ->
                userRepository.getUserRecord(email).observeForever { user ->
                    val contactAgendaList = mutableListOf<ContactAgenda>()

                    for (contact in listContacts) {
                        for (emailUserContact in user.contacts) {
                            if (contact.email == emailUserContact) {
                                contactAgendaList.add(
                                    ContactAgenda(contact.name, contact.phone)
                                )
                            }

                        }
                    }

                    mutableData.value = contactAgendaList
                }

            }
        }

        return mutableData
    }

    fun onAcceptSelected() {
        _navigateToBizumForm.value = Event(true)
    }
}