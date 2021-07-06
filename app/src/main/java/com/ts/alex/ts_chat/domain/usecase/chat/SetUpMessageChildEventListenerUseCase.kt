package com.ts.alex.ts_chat.domain.usecase.chat

import com.ts.alex.ts_chat.domain.models.Message
import com.ts.alex.ts_chat.domain.models.User
import com.ts.alex.ts_chat.domain.repository.FirebaseRepository

class SetUpMessageChildEventListenerUseCase(
    private val fbRepository: FirebaseRepository
) {

    operator fun invoke(recipient: String, callback: (Message) -> Unit){
        fbRepository.setUpMessageChildEventListener(recipient, callback)
    }
}