package com.bluemeth.simbank.src.ui.drawer.contacts

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.core.ex.log
import com.bluemeth.simbank.src.data.models.User
import com.bluemeth.simbank.src.data.providers.firebase.AuthenticationRepository
import com.bluemeth.simbank.src.data.providers.firebase.NotificationRepository
import com.bluemeth.simbank.src.utils.Constants
import com.bluemeth.simbank.src.utils.Methods
import com.squareup.picasso.Picasso
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ContactsRVAdapter @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val authenticationRepository: AuthenticationRepository
) : RecyclerView.Adapter<ContactsRVAdapter.ContactHolder>() {

    private lateinit var listener: OnItemsClickListener
    private var listData = mutableListOf<User>()
    val getListData get() = listData
    var addContact = false

    interface OnItemsClickListener {
        fun onViewProfileClick(user: User)
        fun onActionClick(user: User)
        fun onCancelRequestClick(user: User)
        fun onBlockClick(user: User)
    }

    fun setListData(data: MutableList<User>) {
        listData = data
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
        @SuppressLint("SetTextI18n")
        fun bindView(user: User) {
            val img = itemView.findViewById<ImageView>(R.id.ivCircle)
            val capitals = itemView.findViewById<TextView>(R.id.tvCircleName)
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

            if (user.image == Constants.DEFAULT_PROFILE_IMAGE) {
                capitals.text = Methods.splitNameAndSurname(user.name)
            }

            name.text = user.name
            phone.text = Methods.formatPhoneNumber(user.phone)

            if (addContact) {
                runBlocking {
                    notificationRepository.getContactRequests(
                        authenticationRepository.getCurrentUser().email!!,
                        user.email
                    ).observeForever {
                        if(it != null) {
                            action.text = "Cancelar solicitud"
                            iconAction.setImageResource(R.drawable.ic_remove_contact)

                            llActionContact.setOnClickListener {
                                listener.onCancelRequestClick(user)
                            }
                        } else {
                            action.text = "Añadir"
                            iconAction.setImageResource(R.drawable.ic_add_contact)

                            llActionContact.setOnClickListener {
                                listener.onActionClick(user)
                            }
                        }
                    }
                }
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



            llBlockContact.setOnClickListener {
                listener.onBlockClick(user)
            }


        }
    }
}