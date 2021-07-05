package com.ts.alex.ts_chat.domain.usecase.list_users

import com.ts.alex.ts_chat.domain.repository.FirebaseRepository

class SignOutUseCase(
    private val fbRepository: FirebaseRepository
) {
    operator fun invoke(){
        fbRepository.signOutUser()
    }
}