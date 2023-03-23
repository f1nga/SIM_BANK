package com.bluemeth.simbank.src.data.providers

import com.bluemeth.simbank.src.data.models.Mission
import com.bluemeth.simbank.src.utils.Constants

object MissionsProvider {
    fun getListMissions(): MutableList<Mission> {
        return mutableListOf(
            Constants.BIZUM_MISSION,
            Constants.TRANSFER_MISSION
        )
    }
}