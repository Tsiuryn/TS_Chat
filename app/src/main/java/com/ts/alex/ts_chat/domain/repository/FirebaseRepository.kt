package com.ts.alex.ts_chat.domain.repository

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.ts.alex.ts_chat.domain.models.Message
import com.ts.alex.ts_chat.domain.models.User

interface FirebaseRepository {
    fun signInUserWithEmail(email: String, password: String, callback: (Task<AuthResult>) -> Unit)
    fun createUserWithEmail(
        name: String,
        email: String,
        password: String,
        callback: (Task<AuthResult>) -> Unit
    )

    fun isUserRegistered(): Boolean

    fun getUsers(callback: (User) -> Unit)

    fun signOutUser()

    suspend fun sendMessage(message: Message, recipientToken: String)

    fun setUpUserChildEventListener(callback: (String) -> Unit)

    fun setUpMessageChildEventListener(recipient: String, callback: (Message) -> Unit)

    fun uploadImage(userName: String, recipient: String, selectImageUri: Uri)
}