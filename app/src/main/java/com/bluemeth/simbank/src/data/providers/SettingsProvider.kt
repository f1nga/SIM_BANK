package com.bluemeth.simbank.src.data.providers


object SettingsProvider {
    fun getListSettings():MutableList<String>{
        return mutableListOf(
            "Idioma",
            "Mi perfil",
            "Elejir icono app",
            "Gestión de notificaciones",
            "Acceso con huella dactilar",
            "Cambiar contraseña imagen",
            "Gestionar permisos",
            "Desistimiento y baja del Servicio",
            "Aviso legal y tarifas",
            "Consentimientos actividad comercial",
            "Consentimiento biometrico",
            "Ejercicio de derechos",
            "Consultar el contrato imaginBank",
            "Version 1.0.0"
        )
    }
}