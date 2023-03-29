package com.bluemeth.simbank.src.utils

import com.bluemeth.simbank.src.data.models.Mission

object Constants {
    const val BIZUM_NAME_MISSION = "Realiza un bizum"
    const val TRANSFER_NAME_MISSION = "Realiza una transferencia"

    val BIZUM_MISSION = Mission(BIZUM_NAME_MISSION, 70)
    val TRANSFER_MISSION = Mission(TRANSFER_NAME_MISSION, 80)


}