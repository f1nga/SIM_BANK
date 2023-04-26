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

    val DEFAULT_PROFILE_IMAGE = "https://firebasestorage.googleapis.com/v0/b/simbank-334b7.appspot.com/o/images%2Fprofile%2Fdefaultprofile.png?alt=media&token=97421ce8-993a-40eb-9c20-2f9d2cc8178f"

    val FORM_TYPE = "form_type"
    val SEND_MONEY = "Envíar dinero"
    val REQUEST_MONEY = "Solicitar dinero"

    val REUSE = "reuse"
}