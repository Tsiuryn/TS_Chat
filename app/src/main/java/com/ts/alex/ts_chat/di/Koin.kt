package com.ts.alex.ts_chat.di

import android.app.Activity
import com.ts.alex.ts_chat.data.repository_impl.FirebaseRepositoryImpl
import com.ts.alex.ts_chat.domain.repository.FirebaseRepository
import com.ts.alex.ts_chat.domain.usecase.list_users.GetUsersUseCase
import com.ts.alex.ts_chat.domain.usecase.list_users.SignOutUseCase
import com.ts.alex.ts_chat.domain.usecase.sign_in.CheckUserUseCase
import com.ts.alex.ts_chat.domain.usecase.sign_in.CreateUserWithEmailUseCase
import com.ts.alex.ts_chat.domain.usecase.sign_in.SignInUserWithEmailUseCase
import com.ts.alex.ts_chat.presenter.app.MainActivity
import com.ts.alex.ts_chat.presenter.screens.list_users.ListUsersViewModel
import com.ts.alex.ts_chat.presenter.screens.sign_in.SignInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val singleModule = module {
    viewModel { SignInViewModel(
        createUserUseCase = get(),
        signInUserUserCase = get(),
        checkUserUseCase = get()
    )}
    viewModel { ListUsersViewModel(getUsersUseCase = get(), signOutUseCase = get()) }

    factory { CreateUserWithEmailUseCase(fbRepository = get()) as CreateUserWithEmailUseCase }
    factory { SignInUserWithEmailUseCase(fbRepository = get()) as SignInUserWithEmailUseCase }
    factory { CheckUserUseCase(fbRepository = get()) }
    factory { GetUsersUseCase(fbRepository = get()) as GetUsersUseCase }
    factory { SignOutUseCase(fbRepository = get()) as SignOutUseCase }

    factory { FirebaseRepositoryImpl(activity = get()) as FirebaseRepository }
    single { MainActivity() as Activity }
}