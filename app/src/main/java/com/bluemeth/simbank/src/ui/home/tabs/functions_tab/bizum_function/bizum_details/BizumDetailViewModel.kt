package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_details

import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.data.models.Movement
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BizumDetailViewModel @Inject constructor(

) : ViewModel() {
    private lateinit var _movement: Movement
    val movement: Movement
        get() = _movement

    fun setMovement(movement: Movement) {
        _movement = movement
    }
}