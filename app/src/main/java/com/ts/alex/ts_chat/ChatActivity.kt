package com.ts.alex.ts_chat

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ts.alex.ts_chat.domain.models.Message
import com.ts.alex.ts_chat.domain.models.User

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private var listMessages = ArrayList<Message>()

    private lateinit var sendImageButton: ImageView
    private lateinit var sendMessageButton: ImageView
    private lateinit var inputText: EditText
    private lateinit var progress: ProgressBar

    private var userName: String = "Default User"
    private var recipient: String = ""

    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var messagesDatabaseReference: DatabaseReference
    private lateinit var childEventListener: ChildEventListener

    private lateinit var usersDatabaseReference: DatabaseReference
    private lateinit var usersChildEventListener: ChildEventListener

    companion object {
        const val RC_IMAGE_PICKER = 12421
        const val USER_NAME = "USER_NAME"
        const val RECIPIENT_USER_ID = "RECIPIENT_USER_ID"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        db = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        messagesDatabaseReference = db.reference.child("messages")
        usersDatabaseReference = db.reference.child("users")
        storageReference = storage.reference.child("chat_images")



        userName = intent.getStringExtra(USER_NAME)?: "Default User"
        recipient = intent.getStringExtra(RECIPIENT_USER_ID)?: ""

        buildMessageList()


        sendImageButton = findViewById(R.id.vInsertPhoto)
        sendMessageButton = findViewById(R.id.vSendButton)
        inputText = findViewById(R.id.vInputText)

        inputText.filters = arrayOf<InputFilter>(LengthFilter(100))

        sendImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Choose an image"), RC_IMAGE_PICKER)

        }
        sendMessageButton.setOnClickListener {
            if (inputText.text.toString().isNotEmpty()) {
                val message = Message(
                    userName,
                    inputText.text.toString(),
                    auth.currentUser?.uid,
                    recipient,
                    null
                )

                messagesDatabaseReference.push().setValue(message)
                inputText.setText("")
            } else {
                Snackbar.make(it, "Message cant'b be empty", Snackbar.LENGTH_SHORT).show()
            }
            setUpChildEventListener()

        }

    }

    private fun buildMessageList() {
        recyclerView = findViewById(R.id.vListMessagesRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(listMessages)
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signOut -> {
                Firebase.auth.signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun setUpChildEventListener() {
        usersChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                if (user!!.id == FirebaseAuth.getInstance().currentUser!!.uid) {
                    userName = user.name!!
                }
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
        usersDatabaseReference.addChildEventListener(usersChildEventListener)
        childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if(message?.recipient == recipient && message.sender == auth.currentUser?.uid ){
                    message.isMine = true
                    listMessages.add(message)
                    adapter.notifyDataSetChanged()
                }else if(message?.sender == recipient && message.recipient == auth.currentUser?.uid){
                    message.isMine = false
                    listMessages.add(message)
                    adapter.notifyDataSetChanged()
                }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            val selectImageUri = data?.data
            if (selectImageUri != null) {
                val imageReference: StorageReference =
                    storageReference.child(selectImageUri.lastPathSegment!!)
                val uploadTask: UploadTask = imageReference.putFile(selectImageUri)


                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val messages = Message()
                        messages.imageUrl = downloadUri.toString()
                        messages.name = userName
                        messages.sender = auth.currentUser?.uid
                        messages.recipient = recipient
                        messagesDatabaseReference.push().setValue(messages)

                    } else {
                        // Handle failures
                        // ...
                    }
                }

            }
        }
    }


}