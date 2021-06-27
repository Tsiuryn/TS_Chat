package com.ts.alex.ts_chat

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        listView = findViewById(R.id.vMessageListView)
        adapter = ChatAdapter(this, R.layout.item_message, emptyList())
        listView.adapter = adapter
        sendImageButton = findViewById(R.id.vInsertPhoto)
        sendMessageButton = findViewById(R.id.vSendButton)
        inputText = findViewById(R.id.vInputText)
        progress = findViewById(R.id.vProgress)

        inputText.filters = arrayOf<InputFilter>(LengthFilter(100))



        sendImageButton.setOnClickListener{

        }
        sendMessageButton.setOnClickListener{
            if(inputText.text.toString().isNotEmpty()){
                inputText.setText("")
            }else{
                Snackbar.make(it, "Message cant'b be empty", Snackbar.LENGTH_SHORT).show()
            }

            DAOMessage.add(
                Message(
                    name = "Natali",
                    text = "Hello"
                )
            ).addOnSuccessListener {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }


        }

    }
}