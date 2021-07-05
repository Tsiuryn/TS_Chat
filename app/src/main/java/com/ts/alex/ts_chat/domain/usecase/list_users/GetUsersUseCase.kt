package com.ts.alex.ts_chat.domain.usecase.list_users

import com.ts.alex.ts_chat.domain.models.User
import com.ts.alex.ts_chat.domain.repository.FirebaseRepository

class GetUsersUseCase(
    private val fbRepository: FirebaseRepository
) {

    operator fun invoke(callback:(User) -> Unit){
        fbRepository.getUsers(callback)
    }
}