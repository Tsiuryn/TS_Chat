package com.ts.alex.ts_chat.domain.usecase.sign_in

import com.ts.alex.ts_chat.domain.models.FirebaseTask
import com.ts.alex.ts_chat.domain.models.User
import com.ts.alex.ts_chat.domain.repository.FirebaseRepository

class CheckUserUseCase(
    private val fbRepository: FirebaseRepository
) {
    operator fun invoke()= fbRepository.isUserRegistered()
}