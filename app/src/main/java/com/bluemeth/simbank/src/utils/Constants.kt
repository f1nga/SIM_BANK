package com.bluemeth.simbank.src.utils

import com.bluemeth.simbank.src.data.models.Mission

object Constants {
    val SEND_BIZUM_MISSION = Mission("Realiza un bizum", 70)
    val TRANSFER_MISSION = Mission("Realiza una transferencia", 80)
    val ADD_CONTACT_MISSION = Mission("Envía una solicitud de amistad", 50)
    val PROMOTION_MISSION = Mission("Participa en una promoción", 30)
    val REQUEST_BIZUM_MISSION = Mission("Envía una solicitud de bizum", 60)
    val REUSE_BIZUM_MISSION = Mission("Reutiliza un bizum", 50)
    val REUSE_TRANSFER_MISSION = Mission("Reutiliza una transferencia", 50)

}