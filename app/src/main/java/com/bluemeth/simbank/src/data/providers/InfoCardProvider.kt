package com.bluemeth.simbank.src.data.providers

import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.credit_cards_tab.add_credit_card.model.CreditCardInfo

class InfoCardProvider {
    companion object {
        fun getListInfoCard(): MutableList<CreditCardInfo> {
            return mutableListOf(
                CreditCardInfo(R.drawable.visacredito, "Tarjeta de credito","Perfecta para viajar"," - Una tarjeta de crédito es un tipo de tarjeta\n\nque te permite comprar cosas ahora y pagar\n\npor ellas más tarde. Es decir, el banco o entidad\n\nfinanciera te presta dinero para que hagas tus\n\ncompras y luego debes pagar ese dinero de vuelta,\n\ngeneralmente en cuotas mensuales. Cada vez que\n\nusas tu tarjeta de crédito, estás gastando el dinero\n\ndel banco y acumulando una deuda que debes pagar\n\nmás tarde con intereses."),
                CreditCardInfo(R.drawable.visadebito, "Tarjeta de debito","Comodidad y seguridad", " - Una tarjeta de débito es una tarjeta que está\n\nvinculada directamente a tu cuenta bancaria.\n\nCuando haces una compra con una tarjeta de débito,\n\nel dinero se deduce inmediatamente de tu cuenta\n\nbancaria. Es decir, no estás tomando prestado\n\n dinero, sino que estás gastando tu propio dinero.\n\nSi no tienes suficiente dinero en tu cuenta,\n\nno podrás hacer la compra."),
                CreditCardInfo(R.drawable.visaprepago, "Tarjeta prepago","Recarga y disfruta", "- Una tarjeta de prepago es una tarjeta que\n\ndebes cargar con dinero antes de poder usarla.\n\nEs decir,le das dinero al banco o entidad financiera\n\ny ellos te dan una tarjeta que puedes usar para hacer\n\n compras hasta que el dinero se agote.Cuando\n\nse te acabe el dinero, tendrás que volver a\n\ncargar la tarjeta si quieres seguir usándola.\n\nEs como si tuvieras un monedero electrónico\n\ncon el dinero que cargaste en la tarjeta.")
            )
        }
    }
}