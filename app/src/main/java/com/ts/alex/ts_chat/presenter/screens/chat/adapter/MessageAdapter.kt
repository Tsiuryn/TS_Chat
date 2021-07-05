package com.ts.alex.ts_chat.presenter.screens.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ts.alex.ts_chat.R
import com.ts.alex.ts_chat.domain.models.Message

class MessageAdapter(
    private val listMessages: ArrayList<Message>
) : RecyclerView.Adapter<MessageAdapter.Holder>() {

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val myContainer: LinearLayout = itemView.findViewById(R.id.myMessageContainer)
        val myImage: ImageView = itemView.findViewById(R.id.myImage)
        val myMessage: TextView = itemView.findViewById(R.id.myMessage)

        val yourContainer: LinearLayout = itemView.findViewById(R.id.yourMessageContainer)
        val yourImage: ImageView = itemView.findViewById(R.id.yourImage)
        val yourMessage: TextView = itemView.findViewById(R.id.yourMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val message = listMessages[position]

        if (message.isMine) {
            holder.yourContainer.visibility = View.GONE
            holder.myMessage.text = message.text
            Glide.with(holder.itemView).load(message.imageUrl).into(holder.myImage)
        } else {
            holder.myContainer.visibility = View.GONE
            holder.yourMessage.text = message.text
            Glide.with(holder.itemView).load(message.imageUrl).into(holder.yourImage)
        }
    }

    override fun getItemCount() = listMessages.size
}