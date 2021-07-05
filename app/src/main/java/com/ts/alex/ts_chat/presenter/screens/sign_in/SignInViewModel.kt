package com.ts.alex.ts_chat.presenter.screens.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ts.alex.ts_chat.domain.models.FirebaseTask
import com.ts.alex.ts_chat.domain.models.User
import com.ts.alex.ts_chat.domain.usecase.sign_in.CheckUserUseCase
import com.ts.alex.ts_chat.domain.usecase.sign_in.CreateUserWithEmailUseCase
import com.ts.alex.ts_chat.domain.usecase.sign_in.SignInUserWithEmailUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val createUserUseCase: CreateUserWithEmailUseCase,
    private val signInUserUserCase: SignInUserWithEmailUseCase,
    private val checkUserUseCase: CheckUserUseCase
): ViewModel() {

    private val _registrationResult = MutableSharedFlow<FirebaseTask>()
    val registrationResult: Flow<FirebaseTask>
        get() = _registrationResult




    fun createUser(name: String, email: String, password: String){
        createUserUseCase (
            User(name = name, email = email, password = password)
        ){
            viewModelScope.launch (Dispatchers.IO){
                _registrationResult.emit(it)
            }
        }
    }

    fun signInUser(email: String, password: String){
        signInUserUserCase(
            User(email = email, password = password)
        ){
            viewModelScope.launch (Dispatchers.IO){
                _registrationResult.emit(it)
            }

        }
    }

    fun checkUser() = checkUserUseCase()
}