package com.ts.alex.ts_chat

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object DAOMessage {
    private lateinit var databaseReference: DatabaseReference

    init {
        val db: FirebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = db.getReference(DAOMessage::class.java.simpleName)
    }

    fun add (message: Message): Task<Void>{
        return  databaseReference.push().setValue(message)
    }
}