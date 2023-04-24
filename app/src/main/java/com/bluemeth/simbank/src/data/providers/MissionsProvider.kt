package com.bluemeth.simbank.src.data.providers

import com.bluemeth.simbank.src.data.models.Mission
import com.bluemeth.simbank.src.utils.Constants

object MissionsProvider {
    fun getListMissions(): MutableList<Mission> {
        return mutableListOf(
            Constants.SEND_BIZUM_MISSION,
            Constants.TRANSFER_MISSION,
            Constants.ADD_CONTACT_MISSION,
            Constants.PROMOTION_MISSION,
            Constants.REQUEST_BIZUM_MISSION
        )
    }
}