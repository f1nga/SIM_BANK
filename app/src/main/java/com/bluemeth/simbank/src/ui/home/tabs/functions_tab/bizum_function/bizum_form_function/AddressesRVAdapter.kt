package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.ContactBizum
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class AddressesRVAdapter @Inject constructor() :
    RecyclerView.Adapter<AddressesRVAdapter.AddresseHolder>() {
    private lateinit var listener: OnItemClickListener
    private var _listData = mutableListOf<ContactBizum>()

    interface OnItemClickListener {
        fun onItemClick(contactBizum: ContactBizum)
    }

    fun getListData() : MutableList<ContactBizum> {
        return _listData
    }

    fun setUserBizum(contactBizum: ContactBizum) {
        _listData.forEach { if(it.name == contactBizum.name) return }
        _listData.add(contactBizum)
    }

    fun deleteUserBizum(contactBizum: ContactBizum) {
        _listData.remove(contactBizum)
    }

    fun setItemListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setImportsToContacts(import: Double) {
        _listData.forEach { it.import = import }
    }

    fun getTotalImport() : Double{
        var totalImport = 0.0
        _listData.forEach {
            totalImport += it.import
        }

        return totalImport
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddresseHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.bizum_address_item, parent, false)

        return AddresseHolder(v)
    }

    override fun onBindViewHolder(holder: AddresseHolder, position: Int) {
        val homeHeader = _listData[position]
        holder.bindView(homeHeader)
    }

    override fun getItemCount(): Int {
        return if (_listData.isNotEmpty()) {
            _listData.size
        } else {
            0
        }
    }

    inner class AddresseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(contactBizum: ContactBizum) {
            val tvCapitals = itemView.findViewById<TextView>(R.id.tvCapitals)
            val tvContactName = itemView.findViewById<TextView>(R.id.tvContactName)
            val tvContactImport = itemView.findViewById<TextView>(R.id.tvContactImport)
            val ivDeleteContact = itemView.findViewById<ImageView>(R.id.ivDeleteContact)

            tvCapitals.text = Methods.splitBeneficiaryName(contactBizum.name)
            if(contactBizum.name == "0") tvContactName.text = contactBizum.phoneNumber.toString()
            else tvContactName.text = contactBizum.name
            tvContactImport.text = Methods.formatMoney(contactBizum.import)

            ivDeleteContact.setOnClickListener {
                listener.onItemClick(contactBizum)
            }

        }
    }
}