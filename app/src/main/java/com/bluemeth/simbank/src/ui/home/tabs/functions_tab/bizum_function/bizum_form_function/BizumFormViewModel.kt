package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function

import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.UserBizum
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BizumFormViewModel @Inject constructor(
    val addressesRVAdapter: AddressesRVAdapter
) : ViewModel() {

}