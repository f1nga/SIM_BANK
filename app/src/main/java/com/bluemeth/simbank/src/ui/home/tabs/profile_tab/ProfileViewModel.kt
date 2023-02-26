package com.bluemeth.simbank.src.ui.home.tabs.profile_tab

import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.data.providers.firebase.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {

    fun updateNameFromDB(email: String, name: String) {
        userRepository.updateUserName(email, name)
    }
}