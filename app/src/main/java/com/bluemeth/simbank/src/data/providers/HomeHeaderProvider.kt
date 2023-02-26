package com.bluemeth.simbank.src.data.providers

import com.bluemeth.simbank.src.ui.home.tabs.home_tab.model.HomeHeader

class HomeHeaderProvider {
    companion object {
        fun getListHeader(money: Double): MutableList<HomeHeader> {
            return mutableListOf(
                HomeHeader(money, "Ver ahorros"),
                HomeHeader(543.5, "Ver gastos"),
                HomeHeader(9283.5, "Ver cuentas"),
                HomeHeader(33.36, "Ver pagos")
            )
        }
    }
}