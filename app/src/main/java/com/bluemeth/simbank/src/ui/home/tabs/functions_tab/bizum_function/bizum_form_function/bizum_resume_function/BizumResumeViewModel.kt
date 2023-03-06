package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_resume_function

import androidx.lifecycle.ViewModel
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.AgendaRVAdapter
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.BizumFormModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BizumResumeViewModel @Inject constructor(
    val agendaRVAdapter: AgendaRVAdapter
) : ViewModel() {

    private var _bizumFormModel : BizumFormModel? = null
    val bizumFormModel: BizumFormModel?
        get() = _bizumFormModel

    fun setBizumFormModel(bizumFormModel: BizumFormModel) {
        _bizumFormModel = bizumFormModel
    }
}