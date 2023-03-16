package com.meet.airshield.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.meet.airshield.R
import com.meet.airshield.databinding.ActivityLoginBinding
import com.meet.airshield.utils.Utils

class LoginActivity : BaseActivity(){
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadData()

        binding.txtNotHaveAcc.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnLogIn.setOnClickListener {
            if (isValid()) {
              if (binding.edtEmail.text.toString()!=emailId){
                  Toast.makeText(this, "Please Enter Registered Email", Toast.LENGTH_LONG).show()
              } else if (binding.edtPassword.text.toString()!=password){
                  Toast.makeText(this, "Please Enter Registered Password", Toast.LENGTH_LONG).show()
              }else{
                  isLogin=true
                  saveData()
                  val intent = Intent(this, MainActivity::class.java)
                  startActivity(intent)
                  finish()
              }
            }
        }
        spanText()
    }

    private fun spanText() {
        val ss = SpannableString(getString(R.string.don_t_have_an_account_sign_up))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(p0: View) {
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = (ContextCompat.getColor(this@LoginActivity, R.color.yellow))
//                ds.isUnderlineText = false
//                val typeface: Typeface? =
//                    ResourcesCompat.getFont(this,font.medium)
//                ds.typeface = typeface
            }

        }
        ss.setSpan(clickableSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)

        binding.txtNotHaveAcc.text = ss
        binding.txtNotHaveAcc.highlightColor = Color.TRANSPARENT
        binding.txtNotHaveAcc.movementMethod = LinkMovementMethod()

    }

    private fun isValid(): Boolean {
        if (binding.edtEmail.text?.isEmpty()!! || !Utils.isValideEmail(binding.edtEmail)) {
            binding.txtInputEmail.isErrorEnabled = true
            binding.txtInputEmail.errorIconDrawable = null
            binding.txtInputEmail.error = "Please Enter valid email"
            return false
        } else if (binding.edtPassword.text?.isEmpty()!! || !Utils.isValidPassword(binding.edtPassword)) {
            binding.txtInputEmail.error = null
            binding.txtInputEmail.isErrorEnabled = true
            binding.txtInputPassword.errorIconDrawable = null
            binding.txtInputPassword.error = "Please Enter valid password"
            return false
        } else {
            binding.txtInputEmail.error = null
            binding.txtInputPassword.error = null
            return true
        }
    }
}