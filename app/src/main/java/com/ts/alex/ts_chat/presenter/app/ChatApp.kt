package com.ts.alex.ts_chat.presenter.app

import android.app.Application
import com.ts.alex.ts_chat.di.singleModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class ChatApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            androidLogger(Level.DEBUG)
            modules(listOf(singleModule))
        }
    }
}