package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account

data class SearchMovementsViewState (
    val isValidSinceImport: Boolean = true,
    val isValidUntilImport: Boolean = true,

) {
    fun importsValidated() = isValidSinceImport && isValidUntilImport
}