package com.bluemeth.simbank.src.ui.drawer.contacts.blocked_contacts

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.ui.drawer.contacts.ContactsRVAdapter
import com.bluemeth.simbank.src.utils.Constants
import com.bluemeth.simbank.src.utils.Methods
import com.squareup.picasso.Picasso
import javax.inject.Inject

class BlockedContactsRVAdapter @Inject constructor() :
    RecyclerView.Adapter<BlockedContactsRVAdapter.ContactHolder>() {
    private lateinit var listener: OnItemsClickListener
    private var listData = mutableListOf<User>()
    val getListData get() = listData

    interface OnItemsClickListener {
        fun onClick(user: User)
    }

    fun setListData(data: MutableList<User>) {

        listData = data
    }

    fun setItemListener(listener: BlockedContactsRVAdapter.OnItemsClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_blocked_contact, parent, false)

        return ContactHolder(v)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val user = listData[position]
        holder.bindView(user)
    }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            listData.size
        } else {
            0
        }
    }

    inner class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindView(user: User) {
            val img = itemView.findViewById<ImageView>(R.id.ivCircle)
            val capitals = itemView.findViewById<TextView>(R.id.tvCircleName)
            val name = itemView.findViewById<TextView>(R.id.tvName)
            val phone = itemView.findViewById<TextView>(R.id.tvPhone)
            val ivDesblock = itemView.findViewById<ImageView>(R.id.ivDesblock)

            Picasso.get().load(user.image).into(img)

            if(user.image == Constants.DEFAULT_PROFILE_IMAGE) {
                capitals.text = Methods.splitNameAndSurname(user.name)
            }

            name.text = user.name
            phone.text = Methods.formatPhoneNumber(user.phone)

            ivDesblock.setOnClickListener {
                listener.onClick(user)
            }
        }
    }


}