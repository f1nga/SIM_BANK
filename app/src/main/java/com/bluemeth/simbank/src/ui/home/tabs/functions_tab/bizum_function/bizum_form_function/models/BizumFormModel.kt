package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models

data class BizumFormModel(
    var import: String,
    var subject: String,
    var addressesList: MutableList<ContactBizum>? = null
) {
    fun isNotEmpty() = import.isNotEmpty() && addressesList!!.isNotEmpty()
    fun clearArguments() {
        import = ""
        subject = ""
        addressesList?.clear()
    }
}
