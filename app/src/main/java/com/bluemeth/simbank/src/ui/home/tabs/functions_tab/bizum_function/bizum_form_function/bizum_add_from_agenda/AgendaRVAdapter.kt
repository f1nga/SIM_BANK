package com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.ui.home.tabs.functions_tab.bizum_function.bizum_form_function.bizum_add_from_agenda.model.ContactAgenda
import com.bluemeth.simbank.src.utils.Methods
import java.util.*
import javax.inject.Inject

class AgendaRVAdapter @Inject constructor() :
    RecyclerView.Adapter<AgendaRVAdapter.UserAgendaHolder>() {
    private lateinit var listener: OnItemClickListener
    private var _listData = mutableListOf<ContactAgenda>()
    val listData: MutableList<ContactAgenda> get() = _listData
    var showCheckBox = true

    interface OnItemClickListener {
        fun onItemClick(contactAgenda: ContactAgenda)
    }

    fun setListData(data: MutableList<ContactAgenda>) {
        _listData = data
    }

    fun addToListData(contactAgenda: ContactAgenda) {
        _listData.add(contactAgenda)
    }

    fun setItemListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAgendaHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_from_agenda, parent, false)

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
        fun bindView(contactAgenda: ContactAgenda) {
            val tvCapitals = itemView.findViewById<TextView>(R.id.tvCapitals)
            val tvContactName = itemView.findViewById<TextView>(R.id.tvContactName)
            val tvPhoneNumber = itemView.findViewById<TextView>(R.id.tvPhoneNumber)
            val cbSearchContact = itemView.findViewById<CheckBox>(R.id.cbSearchContact)
            val cvCircle = itemView.findViewById<CardView>(R.id.cvCircle)
            val rnd = Random()
            val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            cvCircle.setCardBackgroundColor(color)

            if(!showCheckBox) {
                cbSearchContact.isVisible = false
//                if(contactAgenda.name == 0) tvContactName.text = "Telefono"
            }

            tvCapitals.text = Methods.splitBeneficiaryName(contactAgenda.name)
            tvContactName.text = contactAgenda.name
            tvPhoneNumber.text = contactAgenda.phoneNumber.toString()
            if (contactAgenda.isChecked) cbSearchContact.isChecked = true

            itemView.setOnClickListener {
                contactAgenda.isChecked = !contactAgenda.isChecked
                cbSearchContact.isChecked = !cbSearchContact.isChecked
                listener.onItemClick(contactAgenda)
            }

        }
    }
}