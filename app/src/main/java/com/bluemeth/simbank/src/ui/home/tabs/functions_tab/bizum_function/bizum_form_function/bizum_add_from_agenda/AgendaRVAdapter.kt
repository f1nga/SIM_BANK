package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.AddressesRVAdapter
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.models.UserAddFromAgenda
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class AgendaRVAdapter @Inject constructor() :
    RecyclerView.Adapter<AgendaRVAdapter.UserAgendaHolder>() {
    private lateinit var listener: OnItemClickListener
    private var _listData = mutableListOf<UserAddFromAgenda>()

    interface OnItemClickListener {
        fun onItemClick(userAddFromAgenda: UserAddFromAgenda)
    }

    fun setListData(data: MutableList<UserAddFromAgenda>) {
        _listData = data
    }

    fun setItemListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAgendaHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.add_from_agenda_item, parent, false)

        return UserAgendaHolder(v)
    }

    override fun onBindViewHolder(holder: UserAgendaHolder, position: Int) {
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

    inner class UserAgendaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(userAddFromAgenda: UserAddFromAgenda) {
            val tvCapitals = itemView.findViewById<TextView>(R.id.tvCapitals)
            val tvContactName = itemView.findViewById<TextView>(R.id.tvContactName)
            val tvPhoneNumber = itemView.findViewById<TextView>(R.id.tvPhoneNumber)
            val ivSearchContact = itemView.findViewById<CheckBox>(R.id.ivSearchContact)

            tvCapitals.text = Methods.splitBeneficiaryName(userAddFromAgenda.name)
            tvContactName.text = userAddFromAgenda.name
            tvPhoneNumber.text = userAddFromAgenda.phoneNumber.toString()

            ivSearchContact.setOnClickListener {
                listener.onItemClick(userAddFromAgenda)
            }

        }
    }
}