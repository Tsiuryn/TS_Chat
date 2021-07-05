package com.ts.alex.ts_chat.domain.usecase.sign_in

import com.ts.alex.ts_chat.domain.models.FirebaseTask
import com.ts.alex.ts_chat.domain.models.User
import com.ts.alex.ts_chat.domain.repository.FirebaseRepository

class SignInUserWithEmailUseCase(
    private val fbRepository: FirebaseRepository
) {
    operator fun invoke(user: User, callback:(FirebaseTask) -> Unit){
        fbRepository.signInUserWithEmail(
            email = user.email!!,
            password = user.password!!
        ){
            if(it.isSuccessful){
                callback(FirebaseTask(
                    isSuccess = true,
                ))
            }else{
                callback(
                    FirebaseTask(
                        isSuccess = false,
                        message = it.exception.toString()
                    )
                )
            }
        }

    }
}