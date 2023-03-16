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
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.meet.airshield.R
import com.meet.airshield.databinding.ActivitySignUpBinding
import com.meet.airshield.utils.Utils

class SignUpActivity : BaseActivity() {
    lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSpinner()
        spanText()

        binding.btnRegister.setOnClickListener {
            if (isValid()) {
                isRegistered = true
                isLogin = true
                password = binding.edtTextPassRegister.text.toString()
                phoneNumber = binding.edtPhoneNumberRegister.text.toString()
                emailId = binding.edtEmailAddress.text.toString()
                city = binding.edtCityRegister.selectedItem.toString()
                saveData()
                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        binding.txtSignInRegister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setSpinner() {
        val spinner = binding.edtCityRegister
        val arrayList: ArrayList<String> = ArrayList()
        val layout = LinearLayout(applicationContext)
        layout.orientation = LinearLayout.VERTICAL

        arrayList.add("Ahmedabad")
        arrayList.add("Mumbai")
        arrayList.add("Gandhinagar")
        arrayList.add("Ankleshwar")
        arrayList.add("Vapi")
        arrayList.add("Nashik")
        arrayList.add("Aurangabad")

        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arrayList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
    }

    private fun spanText() {
        val ss = SpannableString(getString(R.string.already_have_an_account_login))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(p0: View) {
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = (ContextCompat.getColor(this@SignUpActivity, R.color.yellow))

            }

        }
        ss.setSpan(clickableSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)

        binding.txtSignInRegister.text = ss
        binding.txtSignInRegister.highlightColor = Color.TRANSPARENT
        binding.txtSignInRegister.movementMethod = LinkMovementMethod()

    }

    private fun isValid(): Boolean {
        var error:Boolean=false
        if (binding.edtFirstName.text.toString().isEmpty()) {
            binding.edtFirstName.error = "Enter first name"
            error=true
        }
        if (binding.edtLastName.text.toString().isEmpty()) {
            binding.edtLastName.error = "Enter first name"
            error=true
        }
        if (binding.edtEmailAddress.text.toString()
                .isEmpty() || !Utils.isValideEmail(binding.edtEmailAddress)
        ) {
            binding.edtEmailAddress.error = "Enter valid email"
            error=true
        }
        if (binding.edtTextPassRegister.text.toString()
                .isEmpty() || !Utils.isValidPassword(binding.edtTextPassRegister)
        ) {
            binding.txtInputPassword.errorIconDrawable = null
//            binding.txtInputPassword.error =
            Toast.makeText(this, "Enter valid password", Toast.LENGTH_SHORT).show()
            error=true
        }

        if (binding.edtTextPassConfirmRegister.text.toString() != binding.edtTextPassRegister.text.toString()) {
            binding.txtInputPassword.isErrorEnabled = false
            binding.txtConfirmInputPassword.isErrorEnabled = false
            binding.txtInputPassword.errorIconDrawable = null
            Toast.makeText(this, "Please Enter same Password", Toast.LENGTH_SHORT).show()
            error=true
        }

        if (binding.edtStreetAddressRegister.text.toString().isEmpty()) {
            binding.edtStreetAddressRegister.error = "Enter Department Name"
            error=true
        }
        if (binding.edtApptSuiteRegister.text.toString().isEmpty()) {
            binding.edtApptSuiteRegister.error = "Enter Department Type"
            error=true
        }
        if (binding.edtPhoneNumberRegister.text.toString().isEmpty()) {
            binding.edtPhoneNumberRegister.error = "Enter phone number"
            error=true
        }
//        if (binding.edtCityRegister.text.toString().isEmpty()) {
//            binding.edtCityRegister.error = "Enter city name"
//        }
        if (binding.edtStateRegister.text.toString().isEmpty()) {
            binding.edtStateRegister.error = "Enter state name"
            error=true
        }
        if (binding.edtZipCodeRegister.text.toString().isEmpty()) {
            binding.edtZipCodeRegister.error = "Enter zip code name"
            error=true
        }
        if (error){
            return false
        }else{
            return true
        }

    }
}