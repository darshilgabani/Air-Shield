package com.meet.airshield.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.meet.airshield.appHelper.AppHelper.isLogin
import com.meet.airshield.appHelper.AppHelper.loadData
import com.meet.airshield.databinding.ActivitySplashBinding


class SplashActivity : BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Handler().postDelayed({
            loadData(this)
            if (isLogin){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, LandingPageActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 2000)
    }
}