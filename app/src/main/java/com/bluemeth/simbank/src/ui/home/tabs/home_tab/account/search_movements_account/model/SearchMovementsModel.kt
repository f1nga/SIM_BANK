package com.bluemeth.simbank.src.ui.home.tabs.home_tab.account.search_movements_account.model

import java.util.*

data class SearchMovementsModel(
    val text: String?,
    val sinceDate : Date?,
    val untilDate: Date,
    val type: SearchMovementsType?,
    val sinceImport: Double?,
    val untilImport: Double?
)
