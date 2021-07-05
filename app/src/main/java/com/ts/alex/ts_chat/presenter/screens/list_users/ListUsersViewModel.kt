package com.ts.alex.ts_chat.presenter.screens.list_users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ts.alex.ts_chat.domain.models.User
import com.ts.alex.ts_chat.domain.usecase.list_users.GetUsersUseCase
import com.ts.alex.ts_chat.domain.usecase.list_users.SignOutUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class ListUsersViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _users = MutableSharedFlow<User>()
    val users: Flow<User>
        get() = _users

    fun getUsers() {
        getUsersUseCase {
            viewModelScope.launch {
                _users.emit(it)
            }
        }
    }

    fun signOut() {
        signOutUseCase()
    }
}