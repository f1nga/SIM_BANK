package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.model

data class SearchImportsModel(
    val sinceImport: Double,
    val untilImport: Double
) {
    fun isNotEmpty() = sinceImport != 0.0 && untilImport != 0.0
}