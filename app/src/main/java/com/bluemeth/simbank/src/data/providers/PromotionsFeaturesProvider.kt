package com.bluemeth.simbank.src.data.providers

import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.promotions_function.models.PromotionsFeatures

object PromotionsFeaturesProvider {

    fun getListInfoCard(): MutableList<PromotionsFeatures> {
        return mutableListOf(
            PromotionsFeatures(R.drawable.featured_tv_promotions,"Tv Samsung 55", "Por 455€"),
            PromotionsFeatures(R.drawable.featured_iphone_promotions,"Iphone 14 PM", "Por 960€"),
            PromotionsFeatures(R.drawable.featured_oculus_promotions,"Oculus Rift", "Por 599€"),
            )
    }
}