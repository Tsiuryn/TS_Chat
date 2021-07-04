package com.ts.alex.ts_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private val list: ArrayList<User>,
    private val userListener: UserClickListener
) : RecyclerView.Adapter<UserAdapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_user, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val user = list[position]

        holder.avatar.setImageResource(user.avatarMockUpResource)
        holder.userText.text = user.name

        holder.itemView.setOnClickListener{
            userListener.onClick(position)
        }
    }

    override fun getItemCount() = list.size

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.avatarImage)
        val userText: TextView = itemView.findViewById(R.id.userNameText)
    }

    interface UserClickListener{
        fun onClick(position: Int)
    }
}