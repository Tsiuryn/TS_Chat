package com.ts.alex.ts_chat.data.repository_impl

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ts.alex.ts_chat.R
import com.ts.alex.ts_chat.data.network.RetrofitInstance
import com.ts.alex.ts_chat.domain.models.Message
import com.ts.alex.ts_chat.domain.models.NotificationData
import com.ts.alex.ts_chat.domain.models.PushNotification
import com.ts.alex.ts_chat.domain.models.User
import com.ts.alex.ts_chat.domain.repository.FirebaseRepository

const val TAG = "FirebaseRepositoryImpl"
const val TOPIC = "/topics/myTopic2"

class FirebaseRepositoryImpl(
    private val activity: Activity
) : FirebaseRepository {
    private var auth = FirebaseAuth.getInstance()
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var userDatabaseReference: DatabaseReference = db.reference.child("users")
    private var userChildEventListener: ChildEventListener? = null

    private var storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var storageReference: StorageReference = storage.reference.child("chat_images")

    private var messagesDatabaseReference: DatabaseReference = db.reference.child("messages")
    private var messageChildEventListener: ChildEventListener? = null
    private var usersChildEventListener: ChildEventListener? = null

    private var usersDatabaseReference: DatabaseReference = db.reference.child("users")

    override fun signInUserWithEmail(
        email: String,
        password: String,
        callback: (Task<AuthResult>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                callback(task)
            }
    }

    override fun createUserWithEmail(
        name: String,
        email: String,
        password: String,
        callback: (Task<AuthResult>) -> Unit
    ) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { task ->
                    callback(task)
                    if (task.isSuccessful) {
                        createUser(auth.currentUser, name, it)
                    }
                }
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
    }

    override fun isUserRegistered() = auth.currentUser != null

    private fun createUser(user: FirebaseUser?, name: String, token: String) {
        val user = User(
            name = name,
            email = user?.email ?: "",
            id = user?.uid ?: "",
            token = token
        )
        userDatabaseReference.push().setValue(
            user
        )
    }


    override fun getUsers(callback: (User) -> Unit) {
        userDatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
        if (userChildEventListener == null) {
            userChildEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.getValue(User::class.java)
                    user?.avatarMockUpResource = R.drawable.user_image
                    if (user != null && user.id != auth.currentUser?.uid) {
                        callback(user)
                    }

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}
            }
            userDatabaseReference.addChildEventListener(userChildEventListener as ChildEventListener)
        }
    }

    override fun signOutUser() {
        auth.signOut()
    }

    override suspend fun sendMessage(message: Message, recipientToken: String) {
        message.sender = auth.currentUser?.uid
        messagesDatabaseReference.push().setValue(message)
        sendNotification(
            notification = PushNotification(
                data = NotificationData(
                    title = message.name ?: "Default user",
                    message = message.text ?: ""
                ),
                to = recipientToken
            )
        )

    }


    private suspend fun sendNotification(notification: PushNotification) {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                Log.d(TAG, "sendNotification: ${response.body()}")

            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    override fun setUpUserChildEventListener(callback: (String) -> Unit) {
        if (userChildEventListener == null) {

            usersChildEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.getValue(User::class.java)
                    if (user!!.id == FirebaseAuth.getInstance().currentUser!!.uid) {
                        callback(user.name!!)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}

            }
            usersDatabaseReference.addChildEventListener(usersChildEventListener as ChildEventListener)

        }
    }

    override fun setUpMessageChildEventListener(recipient: String, callback: (Message) -> Unit) {
        if (messageChildEventListener == null) {
            messageChildEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)
                    if (message!!.recipient == recipient && message.sender == auth.currentUser?.uid) {
                        message.isMine = true
                        callback(message)
                    } else if (message.sender == recipient && message.recipient == auth.currentUser?.uid) {
                        message.isMine = false
                        callback(message)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}
            }
            messagesDatabaseReference.addChildEventListener(messageChildEventListener as ChildEventListener)

        }
    }

    override fun uploadImage(userName: String, recipient: String, selectImageUri: Uri) {
        val imageReference: StorageReference =
            storageReference.child(selectImageUri.lastPathSegment!!)
        val uploadTask: UploadTask = imageReference.putFile(selectImageUri)

        uploadTask.continueWithTask { task ->
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
            }
        }
    }
}