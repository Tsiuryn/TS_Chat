package com.ts.alex.ts_chat.presenter.screens.sign_in.util

fun isValidName(name: String) = name.isNotEmpty()

fun isValidEmail(email: String): Boolean{
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPassword(psw: String) = psw.length > 5

fun isValidRptPsw(psw: String, rptPsw: String) = psw == rptPsw