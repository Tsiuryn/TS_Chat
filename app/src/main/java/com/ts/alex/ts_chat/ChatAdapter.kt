package com.ts.alex.ts_chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class ChatAdapter(context: Context, resource: Int, val listMessages: List<Message>) :
    ArrayAdapter<Message>(context, resource, listMessages) {


    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var myView = convertView
        if(myView == null){
            myView =
                (context as Activity).layoutInflater.inflate(R.layout.item_message, parent, false)
        }

        val message  = listMessages[position]

        val photo: ImageView = myView!!.findViewById(R.id.vImage)
        val viewMessage: TextView = myView.findViewById(R.id.vText)
        val name: TextView = myView.findViewById(R.id.vName)

        val isText: Boolean = message.imageUrl == null

        if(isText){
            viewMessage.visibility = View.VISIBLE
            photo.visibility = View.GONE
            viewMessage.text = message.text
        }else{
            viewMessage.visibility = View.GONE
            photo.visibility = View.VISIBLE
            Glide.with(myView.context).load(message.imageUrl).into(photo)
        }

        name.text = message.name

        return myView
    }

}