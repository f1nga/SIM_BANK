package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.data.models.TargetPay
import com.bluemeth.simbank.src.data.models.Movement
import com.bluemeth.simbank.src.data.providers.firebase.TargetPayRepository
import com.bluemeth.simbank.src.data.providers.firebase.MovementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeTabViewModel @Inject constructor(
    private val targetPayRepository: TargetPayRepository,
    val headerAdapter: HorizontalListRVAdapter,
    val transfersAdapter: TransfersRVAdapter,
    val movementAdapter: MovementRecordsRVAdapter,
    private val transfersRepository: MovementRepository
) : ViewModel() {
    fun getMovementsFromDB(owner: String): MutableLiveData<MutableList<TargetPay>> {
        val mutableData = MutableLiveData<MutableList<TargetPay>>()

        targetPayRepository.getMovements(owner).observeForever {
            mutableData.value = it
        }
        return mutableData
    }

    fun getTransfersFromDB(email: String): MutableLiveData<MutableList<Movement>> {
        val mutableData = MutableLiveData<MutableList<Movement>>()

        transfersRepository.getTransfersByType(email).observeForever {
            mutableData.value = it
        }

        return mutableData
    }
}