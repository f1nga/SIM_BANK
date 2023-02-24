package com.bluemeth.simbank.src.data.providers

import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader

class HomeHeaderProvider {
    companion object {
        fun getListHeader(money: Double): MutableList<HomeHeader> {
            return mutableListOf(
                HomeHeader(money, "Ver ahorros"),
                HomeHeader(543.5, "Ver gastos"),
                HomeHeader(53.5, "Ver gastos"),
                HomeHeader(543.5, "Ver gastos")
            )
        }
    }
}