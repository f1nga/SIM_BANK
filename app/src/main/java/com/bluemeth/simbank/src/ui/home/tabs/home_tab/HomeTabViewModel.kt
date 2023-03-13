package com.bluemeth.simbank.src.ui.home.tabs.home_tab

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeTabViewModel @Inject constructor(
    val headerAdapter: HorizontalListRVAdapter,
    val transfersAdapter: MovementsRVAdapter,
) : ViewModel() {

}