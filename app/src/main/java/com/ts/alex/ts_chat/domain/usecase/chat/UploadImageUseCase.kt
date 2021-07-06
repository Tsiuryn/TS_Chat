package com.ts.alex.ts_chat.domain.usecase.chat

import android.net.Uri
import com.ts.alex.ts_chat.domain.models.Message
import com.ts.alex.ts_chat.domain.models.User
import com.ts.alex.ts_chat.domain.repository.FirebaseRepository

class UploadImageUseCase(
    private val fbRepository: FirebaseRepository
) {

    operator fun invoke(userName: String, recipient: String, selectImageUri: Uri){
        fbRepository.uploadImage(
            userName = userName,
            recipient = recipient,
            selectImageUri = selectImageUri
        )
    }
}