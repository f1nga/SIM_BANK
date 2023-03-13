package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    val accountMovementsAdapter: AccountMovementsRVAdapter,
) : ViewModel() {

}