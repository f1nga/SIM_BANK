package com.bluemeth.simbank.src.ui.drawer.contacts

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
import com.squareup.picasso.Picasso
import javax.inject.Inject

class ContactsRVAdapter @Inject constructor() :
    RecyclerView.Adapter<ContactsRVAdapter.ContactHolder>() {
    private lateinit var listener: OnItemsClickListener
    private var listData = mutableListOf<User>()
    var addContact = false

    interface OnItemsClickListener {
        fun onViewProfileClick(user: User)
        fun onActionClick(user: User)
        fun onBlockClick(user: User)
    }

    fun setListData(data: MutableList<User>) {
        listData = data
    }

    fun deleteContact(user: User) {
        listData.remove(user)
    }

    fun setItemListener(listener: OnItemsClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)

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
        fun bindView(user: User) {
            val img = itemView.findViewById<ImageView>(R.id.ivCircle)
            val name = itemView.findViewById<TextView>(R.id.tvName)
            val phone = itemView.findViewById<TextView>(R.id.tvPhone)
            val action = itemView.findViewById<TextView>(R.id.tvAction)
            val iconAction = itemView.findViewById<ImageView>(R.id.ivAction)
            val llActions = itemView.findViewById<LinearLayout>(R.id.llActions)
            val llContact = itemView.findViewById<LinearLayout>(R.id.llContact)
            val llViewProfile = itemView.findViewById<LinearLayout>(R.id.llViewProfile)
            val llActionContact = itemView.findViewById<LinearLayout>(R.id.llActionContact)
            val llBlockContact = itemView.findViewById<LinearLayout>(R.id.llBlockContact)

            Picasso.get().load(user.image).into(img)

            name.text = user.name
            phone.text = user.phone.toString()

            if(addContact) {
                action.text = "Añadir"
                iconAction.setImageResource(R.drawable.ic_add_contact)
            } else {
                action.text = "Eliminar"
                iconAction.setImageResource(R.drawable.ic_remove_contact)
            }

            llContact.setOnClickListener {
                llActions.isVisible = !llActions.isVisible
            }

            llViewProfile.setOnClickListener {
                listener.onViewProfileClick(user)
            }

            llActionContact.setOnClickListener {
                listener.onActionClick(user)
            }

            llBlockContact.setOnClickListener {
                listener.onBlockClick(user)
            }
        }
    }
}