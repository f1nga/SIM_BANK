package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.model

data class ContactAgenda(
    val name: String,
    val phoneNumber: Int,
    var isChecked: Boolean = false
)