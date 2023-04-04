package com.bluemeth.simbank.src.ui.drawer.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class NotificationsRVAdapter @Inject constructor() :
    RecyclerView.Adapter<NotificationsRVAdapter.NotificationHolder>() {
    private lateinit var listener: OnItemClickListener
    private var listData = mutableListOf<Notification>()

    interface OnItemClickListener {
        fun onItemClick(notification: Notification)
        fun onDeleteClick(notification: Notification)
        fun onMarkAsReadedClick(notification: Notification)
    }

    fun removeNotification(notification: Notification) {
        listData.remove(notification)
    }

    fun setNotification(notification: Notification) {
        listData.add(notification)
    }

    fun setListData(data: MutableList<Notification>) {
        listData = data
    }

    fun setItemListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)

        return NotificationHolder(v)
    }

    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        val notification = listData[position]
        holder.bindView(notification)
    }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            listData.size
        } else {
            0
        }
    }

    inner class NotificationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(notification: Notification) {
            val date = itemView.findViewById<TextView>(R.id.tvNotiDate)
            val title = itemView.findViewById<TextView>(R.id.tvNotiTitle)
            val description = itemView.findViewById<TextView>(R.id.tvNotiDescription)
            val deleteNoti = itemView.findViewById<ImageView>(R.id.ivDeleteNoti)
            val notiReaded = itemView.findViewById<ImageView>(R.id.tvNotiReaded)
            val markAsReaded = itemView.findViewById<TextView>(R.id.tvNotiMarkAsReaded)

            date.text = Methods.formatLongDate(notification.date.toDate())
            title.text = notification.title
            description.text = notification.description
            markAsReaded.text = if(notification.readed) "Marcar como no leído" else "Marcar como leído"

            notiReaded.isVisible = !notification.readed

            itemView.setOnClickListener { listener.onItemClick(notification) }
            deleteNoti.setOnClickListener { listener.onDeleteClick(notification) }
            markAsReaded.setOnClickListener { listener.onMarkAsReadedClick(notification) }
        }
    }
}