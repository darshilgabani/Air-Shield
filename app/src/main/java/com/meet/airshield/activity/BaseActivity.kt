package com.meet.airshield.activity

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


open class BaseActivity : AppCompatActivity() {
    var isLogin: Boolean = false
    var isRegistered:Boolean=false
    var phoneNumber:String=""
    var password:String=""
    var emailId:String=""
    var city:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun saveData(){
        val sharedPrefrances=getSharedPreferences("meet", Context.MODE_PRIVATE)
        val editor=sharedPrefrances.edit()
        editor.apply{
            putBoolean("IS_REGISTER",isRegistered)
            putBoolean("IS_LOGIN",isLogin)
            putString("PASSWORD",password)
            putString("PHONE_NUMBER",password)
            putString("EMAIL_ID",emailId)
            putString("CITY",city)
        }.apply()
    }

    fun toastMe(messege:String){
        Toast.makeText(this, messege, Toast.LENGTH_SHORT).show()
    }

    fun loadData(){
        val sharedPrefrances=getSharedPreferences("meet", Context.MODE_PRIVATE)
       isRegistered=sharedPrefrances.getBoolean("IS_REGISTER",false)
       isLogin=sharedPrefrances.getBoolean("IS_LOGIN",false)
        password= sharedPrefrances.getString("PASSWORD","").toString()
        phoneNumber=sharedPrefrances.getString("PHONE_NUMBER","").toString()
        emailId=sharedPrefrances.getString("EMAIL_ID","").toString()
        city=sharedPrefrances.getString("CITY","").toString()
    }
}