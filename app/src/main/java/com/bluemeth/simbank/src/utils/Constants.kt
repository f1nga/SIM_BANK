package com.bluemeth.simbank.src.utils

import com.bluemeth.simbank.src.data.models.Mission

object Constants {
    const val BIZUM_NAME_MISSION = "Realiza un bizum"
    const val TRANSFER_NAME_MISSION = "Realiza una transferencia"

    const val BIZUM_EXP_MISSION = 70
    const val TRANSFER_EXP_MISSION = 80

    val BIZUM_MISSION = Mission(BIZUM_NAME_MISSION, BIZUM_EXP_MISSION)
    val TRANSFER_MISSION = Mission(TRANSFER_NAME_MISSION, TRANSFER_EXP_MISSION)


}