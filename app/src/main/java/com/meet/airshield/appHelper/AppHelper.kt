package com.meet.airshield.appHelper

import android.content.Context

object AppHelper {
    var isLogin: Boolean = false
    var isRegistered: Boolean = false
    var phoneNumber: String = ""
    var password: String = ""
    var emailId: String = ""

    fun saveData(context: Context) {
        val sharedPrefrances = context.getSharedPreferences("meet", Context.MODE_PRIVATE)
        val editor = sharedPrefrances.edit()
        editor.apply {
            putBoolean("IS_REGISTER", isRegistered)
            putBoolean("IS_LOGIN", isLogin)
            putString("PASSWORD", password)
            putString("PHONE_NUMBER", password)
            putString("EMAIL_ID", emailId)
        }.apply()
    }


    fun loadData(context: Context) {
        val sharedPrefrances = context.getSharedPreferences("meet", Context.MODE_PRIVATE)
        isRegistered = sharedPrefrances.getBoolean("IS_REGISTER", false)
        isLogin = sharedPrefrances.getBoolean("IS_LOGIN", false)
        password = sharedPrefrances.getString("PASSWORD", "").toString()
        phoneNumber = sharedPrefrances.getString("PHONE_NUMBER", "").toString()
        emailId = sharedPrefrances.getString("EMAIL_ID", "").toString()
    }


}