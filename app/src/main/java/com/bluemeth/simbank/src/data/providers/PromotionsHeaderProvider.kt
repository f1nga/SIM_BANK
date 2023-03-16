package com.bluemeth.simbank.src.data.providers

import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.promotions_function.models.PromotionsHeader

object PromotionsHeaderProvider {

    fun getListInfoCard(): MutableList<PromotionsHeader> {
        return mutableListOf(
            PromotionsHeader(R.drawable.promotions_planet, "Planet"),
            PromotionsHeader(R.drawable.promotions_academy, "Academy"),
            PromotionsHeader(R.drawable.promotions_shop, "Shop"),
            PromotionsHeader(R.drawable.promotions_sales,"Ventajas"),
            PromotionsHeader(R.drawable.promotions_students,"Estudiantes"),
            PromotionsHeader(R.drawable.promotions_music,"Music"),
            PromotionsHeader(R.drawable.promotions_games,"Games"),
            PromotionsHeader(R.drawable.promotions_coffe,"Coffe")
        )
    }
}