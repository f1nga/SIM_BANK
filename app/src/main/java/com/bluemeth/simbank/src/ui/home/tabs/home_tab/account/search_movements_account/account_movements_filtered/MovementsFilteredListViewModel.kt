package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.account_movements_filtered

import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.model.SearchMovementsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovementsFilteredListViewModel @Inject constructor(

) : ViewModel(){
    private lateinit var _searchMovementsModel: SearchMovementsModel
    val searchMovementsModel: SearchMovementsModel
        get() = _searchMovementsModel

    fun setSearchMovementsModel(searchMovementsModel: SearchMovementsModel) {
        _searchMovementsModel = searchMovementsModel
    }
}