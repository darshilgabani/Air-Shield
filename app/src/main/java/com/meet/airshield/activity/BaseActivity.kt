package com.meet.airshield.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.meet.airshield.R
import com.meet.airshield.appHelper.AppHelper.emailId
import com.meet.airshield.appHelper.AppHelper.phoneNumber
import javax.mail.*
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


open class BaseActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    var gpsStatus = false
    var phoneNumberREgister = ""
    var senderMail = "meet.devstree@gmail.com"
    var senderMailPasword = "warszkyujnnnvews"
    var receiverMail = emailId
    private var dialog: Dialog? = null
    var latitude: Double = 0.00
    var longiude: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun toastMe(messege: String) {
        Toast.makeText(this, messege, Toast.LENGTH_SHORT).show()
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(
            context,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestOtherPermissions(): Boolean {
        val PERMISSION_ALL = 1
        val PERMISSIONS = arrayOf(
            Manifest.permission.SEND_SMS, Manifest.permission.POST_NOTIFICATIONS,
        )

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
            return true
        }
        return false
    }

    fun sendMail(message: String) {
        try {
            val stringSenderEmail = senderMail
            val stringReceiverEmail = receiverMail
            val stringPasswordSenderEmail = senderMailPasword //"warszkyujnnnvews"
            val stringHost = "smtp.gmail.com"
            val properties = System.getProperties()
            properties["mail.smtp.host"] = stringHost
            properties["mail.smtp.port"] = "465"
            properties["mail.smtp.ssl.enable"] = "true"
            properties["mail.smtp.auth"] = "true"
            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail)
                }
            })
            val mimeMessage = MimeMessage(session)
            mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(stringReceiverEmail))
            mimeMessage.subject = "Subject: Air Quality Alert"
            mimeMessage.setText(message)
            val thread = Thread {
                try {
                    Transport.send(mimeMessage)
                } catch (e: MessagingException) {
                    e.printStackTrace()
                }
            }
            thread.start()
        } catch (e: AddressException) {
            e.printStackTrace()
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

    fun getNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101
                )
            }
        }
    }

    fun sendSms(message: String) {
        try {
            val smsManager: SmsManager
            if (Build.VERSION.SDK_INT >= 23) {
                smsManager = this.getSystemService(SmsManager::class.java)
            } else {
                smsManager = SmsManager.getDefault()
            }
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(applicationContext, "Message Sent", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("sms send failed", e.message.toString())
        }
    }


//    fun checkGpsStatus() {
//        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        if (gpsStatus) {
//            toastMe("GPS Is Enabled")
//        } else {
//            toastMe("GPS Is Not Enabled")
//        }
//    }

    fun isOnline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    fun showProgressDialog() {
        if (dialog == null) {
            dialog = Dialog(this@BaseActivity)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.setCancelable(false)
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setContentView(R.layout.layout_progress_dialog)

        }

        if (dialog?.isShowing == false) dialog?.show()
    }

    fun hideProgressDialog() {

        if (dialog != null) {
            dialog!!.dismiss()
        }

    }
}