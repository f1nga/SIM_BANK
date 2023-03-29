package com.bluemeth.simbank.src.ui.drawer.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bluemeth.simbank.R
import com.bluemeth.simbank.src.data.models.Notification
import com.bluemeth.simbank.src.utils.Methods
import javax.inject.Inject

class NotificationsRVAdapter @Inject constructor() :
    RecyclerView.Adapter<NotificationsRVAdapter.NotificationHolder>() {
    private lateinit var listener: OnItemClickListener
    private var listData = listOf<Notification>()

    interface OnItemClickListener {
        fun onItemClick(notification: Notification)
    }

    fun setListData(data: List<Notification>) {
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

            date.text = Methods.formatLongDate(notification.date.toDate())
            title.text = notification.title
            description.text = notification.description

            itemView.setOnClickListener {
                listener.onItemClick(notification)
            }
        }
    }
}