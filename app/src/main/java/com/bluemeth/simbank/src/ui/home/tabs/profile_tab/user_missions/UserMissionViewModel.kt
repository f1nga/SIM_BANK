package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.user_missions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluemeth.simbank.src.data.models.CreditCard
import com.bluemeth.simbank.src.data.models.Mission
import com.bluemeth.simbank.src.data.providers.firebase.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Scope

@HiltViewModel
class UserMissionViewModel @Inject constructor(
    private val missionRepository: MissionRepository

):ViewModel() {

    fun getMissionsFromDB(missionsComplete : List<String>): LiveData<MutableList<Mission>> {
        val mutableData = MutableLiveData<MutableList<Mission>>()
            missionRepository.getMissions(missionsComplete).observeForever { missionsList ->
                mutableData.value = missionsList
            }

        return mutableData
    }
}