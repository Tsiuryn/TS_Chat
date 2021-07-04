package com.ts.alex.ts_chat.di

import com.ts.alex.ts_chat.presenter.screens.sign_in.SignInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val singleModule = module {
    viewModel { SignInViewModel()}
//
//    factory { GetNewsRepository(getNewsDataSource = get()) as IGetNewsUseCase }
//    factory { DBRepository(source = get()) as IDBUseCase }
//    factory { SharedPreferencesRepository(source = get()) as ISharedPreferencesUseCase }
//    factory { JobRepository (source = get()) as IJobUseCase }
//
//    factory { GetNewsDataSource(restApi = get()) as IGetNewsDataSource }
//    factory { DBDataSource() as IDBDataSource }
//    factory { SharedPreferencesDataSource(preferences = get()) as ISharedPreferencesDataSource }
//    factory { JobDataSource (context = get()) as IJobDataSource }
//
//    single { providePlaceHolderApi() as RestApi }
//    single { SharedPreference(context = get()) }
}