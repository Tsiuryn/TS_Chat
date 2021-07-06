package com.ts.alex.ts_chat.presenter.screens.chat

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ts.alex.ts_chat.domain.models.Message
import com.ts.alex.ts_chat.domain.usecase.chat.SendMessageUseCase
import com.ts.alex.ts_chat.domain.usecase.chat.SetUpMessageChildEventListenerUseCase
import com.ts.alex.ts_chat.domain.usecase.chat.SetUpUserChildEventListenerUseCase
import com.ts.alex.ts_chat.domain.usecase.chat.UploadImageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val setUpMessageChildEventListenerUseCase: SetUpMessageChildEventListenerUseCase,
    private val setUpUserChildEventListenerUseCase: SetUpUserChildEventListenerUseCase,
    private val uploadImageUseCase: UploadImageUseCase
) : ViewModel() {

    private var _observeUserName = MutableSharedFlow<String>()
    val observeUserName: Flow<String>
    get() = _observeUserName

    private var _observeMessages = MutableSharedFlow<Message>()
    val observeMessages: Flow<Message>
        get() = _observeMessages

    fun sendMessage(message: Message){
        sendMessageUseCase(message = message)
    }

    fun setUpMessageChildEventListener(recipient: String){
        setUpMessageChildEventListenerUseCase(recipient = recipient){message ->
            viewModelScope.launch(Dispatchers.IO){
                _observeMessages.emit(message)
            }
        }
    }

    fun setUpUserChildEventListener(){
        setUpUserChildEventListenerUseCase {userName ->
            viewModelScope.launch(Dispatchers.IO){
                _observeUserName.emit(userName)
            }
        }
    }

    fun uploadImage(userName: String, recipient: String, selectImageUri: Uri){
        uploadImageUseCase(
            userName = userName,
            recipient = recipient,
            selectImageUri = selectImageUri
        )
    }
}