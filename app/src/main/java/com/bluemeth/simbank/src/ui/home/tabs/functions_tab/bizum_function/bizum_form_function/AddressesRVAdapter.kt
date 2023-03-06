package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.UserBizum
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class AddressesRVAdapter @Inject constructor() :
    RecyclerView.Adapter<AddressesRVAdapter.AddresseHolder>() {
    private lateinit var listener: OnItemClickListener
    private var _listData = mutableListOf<UserBizum>()

    interface OnItemClickListener {
        fun onItemClick(userBizum: UserBizum)
    }

    fun getListData() : MutableList<UserBizum> {
        return _listData
    }

    fun setUserBizum(userBizum: UserBizum) {
        _listData.add(userBizum)
    }

    fun deleteUserBizum(userBizum: UserBizum) {
        _listData.remove(userBizum)
    }

    fun setItemListener(listener: OnItemClickListener) {
        this.listener = listener
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
        fun bindView(userBizum: UserBizum) {
            val tvCapitals = itemView.findViewById<TextView>(R.id.tvCapitals)
            val tvContactName = itemView.findViewById<TextView>(R.id.tvContactName)
            val tvContactImport = itemView.findViewById<TextView>(R.id.tvContactImport)
            val ivDeleteContact = itemView.findViewById<ImageView>(R.id.ivDeleteContact)

            tvCapitals.text = Methods.splitBeneficiaryName(userBizum.name)
            tvContactName.text = userBizum.name
            tvContactImport.text = Methods.formatMoney(userBizum.import)

            ivDeleteContact.setOnClickListener {
                listener.onItemClick(userBizum)
            }

        }
    }
}