package com.ts.alex.ts_chat.presenter.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ts.alex.ts_chat.R
import com.ts.alex.ts_chat.presenter.screens.sign_in.SignInFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.vMainContainer, SignInFragment())
            .commit()
    }
}