package com.ts.alex.ts_chat.domain.usecase.chat

import com.ts.alex.ts_chat.domain.models.Message
import com.ts.alex.ts_chat.domain.models.User
import com.ts.alex.ts_chat.domain.repository.FirebaseRepository

class SendMessageUseCase(
    private val fbRepository: FirebaseRepository
) {

    suspend operator fun invoke(message: Message, recipientToken: String) {
        fbRepository.sendMessage(
            message = message,
            recipientToken = recipientToken
        )
    }
}