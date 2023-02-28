package com.bluemeth.simbank.src.ui.home.tabs.profile_tab.update_fields.models

data class UserNameUpdate(
    val name: String,
    val lastName: String,
    val secondName: String,
) {
    fun isNotEmpty() =
        name.isNotEmpty() && lastName.isNotEmpty() && secondName.isNotEmpty()
}
