package com.meet.airshield.utils

import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object Utils {

    fun isNotEmpty(
        fname: AppCompatEditText?,
        tet: TextInputLayout,
        cardView: CardView? = null,
        error: String
    ): Boolean {
        if (fname!!.text.toString().trim().isEmpty()) {
            tet.isErrorEnabled = true
            tet.error = error
            tet.errorIconDrawable = null

            if (cardView != null) {
                cardView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }


            return false
        } else {
            tet.isErrorEnabled = false
            tet.error = null

            if (cardView != null) {
                cardView.layoutParams.height = 150
            }

            return true
        }
    }


    fun isNotEmptyNumber(
        fname: AppCompatEditText?,
        tet: TextInputLayout,
        cardView: CardView? = null,
        error: String
    ): Boolean {
        if (fname!!.text.toString().trim().isEmpty() || fname!!.text.toString().length<10) {
            tet.isErrorEnabled = true
            tet.error = error
            tet.errorIconDrawable = null

            if (cardView != null) {
                cardView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }


            return false
        } else {
            tet.isErrorEnabled = false
            tet.error = null

            if (cardView != null) {
                cardView.layoutParams.height = 150
            }

            return true
        }
    }

    fun isValidPassword(email: EditText?): Boolean {
        val passwordPattern: Pattern =
            Pattern.compile("""^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!\-_?&])(?=\S+$).{4,}""")
        val matcher = passwordPattern.matcher(email!!.text.toString())
        return if (matcher.matches()) {
            true
        } else {
            email.requestFocus()
            false
        }
    }




    fun isValideEmail(email: AppCompatEditText?): Boolean {
        val emailExp = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,10}$"
        val pattern = Pattern.compile(emailExp, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email!!.text.toString())
        return if (matcher.matches()) {
            true
        } else {
            // email.error = "Enter valid email"
            email.requestFocus()
            false
        }
    }

    fun getTodayDate(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return formatter.format(Date(System.currentTimeMillis()))
    }

    fun isValideEmail(email: EditText?, tet: TextInputLayout, card: CardView? = null): Boolean {
        val emailExp = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,10}$"
        val pattern = Pattern.compile(emailExp, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email!!.text.toString())
        return if (matcher.matches()) {
            tet.isErrorEnabled = false
            tet.error = null
            if (card != null) {
                card.layoutParams?.height = 150
            }

            true
        } else {
            tet.isErrorEnabled = true
            tet.error = "Please enter valid email address"
            tet.errorIconDrawable = null
            if (card != null) {
                card.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }

//            email.requestFocus()
            false
        }
    }



    fun isEqualPass(
        passEt: TextInputEditText,
        passTEt: TextInputLayout,
        cPassEt: AppCompatEditText,
        cPassTEt: TextInputLayout,
        cardView: CardView? = null,
        string: String
    ): Boolean {
        if (passEt.text.toString().equals(cPassEt.text.toString())) {
            cPassTEt.isErrorEnabled = false
            cPassTEt.error = null
            if (cardView != null) {
                cardView.layoutParams?.height = 150
            }

            return true
        } else {

            cPassTEt.isErrorEnabled = true
            cPassTEt.error = string

            if (cardView != null) {
                cardView.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }

            return false
        }
    }
}