package com.ts.alex.ts_chat

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ChatAdapter
    private lateinit var sendImageButton: ImageView
    private lateinit var sendMessageButton: ImageView
    private lateinit var inputText: EditText
    private lateinit var progress: ProgressBar

    private var userName: String = "Default User"

    private lateinit var db: FirebaseDatabase
    private lateinit var messagesDatabaseReference: DatabaseReference
    private lateinit var childEventListener: ChildEventListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = FirebaseDatabase.getInstance()
        messagesDatabaseReference = db.reference.child("messages")



        listView = findViewById(R.id.vMessageListView)
        adapter = ChatAdapter(this, R.layout.item_message, ArrayList())
        listView.adapter = adapter
        sendImageButton = findViewById(R.id.vInsertPhoto)
        sendMessageButton = findViewById(R.id.vSendButton)
        inputText = findViewById(R.id.vInputText)

        inputText.filters = arrayOf<InputFilter>(LengthFilter(100))

        sendImageButton.setOnClickListener {

        }
        sendMessageButton.setOnClickListener {
            if (inputText.text.toString().isNotEmpty()) {
                val message = Message(
                    userName,
                    inputText.text.toString(),
                    null

                )

                messagesDatabaseReference.push().setValue(message)
                inputText.setText("")
            } else {
                Snackbar.make(it, "Message cant'b be empty", Snackbar.LENGTH_SHORT).show()
            }
//
//            DAOMessage.add(
//                Message(
//                    name = "Natali",
//                    text = "Hello"
//                )
//            ).addOnSuccessListener {
//                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
//            }.addOnFailureListener{
//                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
//            }
            setUpChildEventListener()

        }

    }

    private fun setUpChildEventListener() {
        childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                adapter.addMessage(message)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        messagesDatabaseReference.addChildEventListener(childEventListener)
    }
}