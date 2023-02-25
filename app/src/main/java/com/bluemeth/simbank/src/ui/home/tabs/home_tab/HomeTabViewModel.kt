package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.providers.firebase.MovementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeTabViewModel @Inject constructor(private val movementRepository: MovementRepository): ViewModel() {
    fun getMovementsFromDB(owner: String):  MutableLiveData<MutableList<Movement>> {
        val mutableData = MutableLiveData<MutableList<Movement>>()

        movementRepository.getMovements(owner).observeForever {
            mutableData.value = it
        }
        return mutableData
    }
}