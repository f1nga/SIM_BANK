package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.movement_details

import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.data.models.Movement

class BizumDetailAccountViewModel : ViewModel() {

    private lateinit var _movement : Movement
    val movement: Movement
        get() = _movement

    fun setMovement(movement: Movement) {
        _movement = movement
    }
}