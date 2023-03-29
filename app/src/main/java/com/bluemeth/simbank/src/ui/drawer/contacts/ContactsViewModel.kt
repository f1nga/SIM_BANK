package com.bluemeth.simbank.src.ui.drawer.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.core.Event
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.NotificationsRepository
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    private val _contactDeleted = MutableLiveData<User>()
    val contactDeleted: LiveData<User>
        get() = _contactDeleted

    fun getUserContactsFromDB(email: String): MutableLiveData<MutableList<User>> {
        val mutableData = MutableLiveData<MutableList<User>>()

        viewModelScope.launch {
            userRepository.getUserContacts(email).observeForever {
                mutableData.value = it
            }
        }

        return mutableData
    }

    fun deleteUserContactFromDB(email: String, deleteUser: User) {
        viewModelScope.launch {
            val userContactDeleted = userRepository.deleteUserContact(email, deleteUser.email)

            if(userContactDeleted) {
                _contactDeleted.value = deleteUser
            } else {
                Timber.tag("Error").e("Notification not sended")
            }
        }
    }
}