package com.meet.airshield.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import javax.mail.*
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


open class BaseActivity : AppCompatActivity() {
    var isLogin: Boolean = false
    var isRegistered: Boolean = false
    var phoneNumber: String = ""
    var password: String = ""
    var emailId: String = ""
    var city: String = ""
    var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationManager: LocationManager
    var gpsStatus = false
    var phoneNumberREgister = ""
    var senderMail = "meet.devstree@gmail.com"
    var senderMailPasword = "warszkyujnnnvews"
    var receiverMail = emailId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun saveData() {
        val sharedPrefrances = getSharedPreferences("meet", Context.MODE_PRIVATE)
        val editor = sharedPrefrances.edit()
        editor.apply {
            putBoolean("IS_REGISTER", isRegistered)
            putBoolean("IS_LOGIN", isLogin)
            putString("PASSWORD", password)
            putString("PHONE_NUMBER", password)
            putString("EMAIL_ID", emailId)
            putString("CITY", city)
        }.apply()
    }

    fun toastMe(messege: String) {
        Toast.makeText(this, messege, Toast.LENGTH_SHORT).show()
    }

    fun loadData() {
        val sharedPrefrances = getSharedPreferences("meet", Context.MODE_PRIVATE)
        isRegistered = sharedPrefrances.getBoolean("IS_REGISTER", false)
        isLogin = sharedPrefrances.getBoolean("IS_LOGIN", false)
        password = sharedPrefrances.getString("PASSWORD", "").toString()
        phoneNumber = sharedPrefrances.getString("PHONE_NUMBER", "").toString()
        emailId = sharedPrefrances.getString("EMAIL_ID", "").toString()
        city = sharedPrefrances.getString("CITY", "").toString()
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it.toString()) == PackageManager.PERMISSION_GRANTED
    }

    fun requestOtherPermissions(){
        val PERMISSION_ALL = 1
        val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.SEND_SMS,
        )

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }
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
                );
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


    fun checkGpsStatus() {
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsStatus) {
            toastMe("GPS Is Enabled")
        } else {
            toastMe("GPS Is Not Enabled")
        }
    }

    fun isOnline(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

//    private fun sendNotification(current: LocalDateTime) {
//        createDefaultChannel()
//        val builder =
//            NotificationCompat.Builder(context, CHANNEL_ID1)
//                .setColor(ContextCompat.getColor(context, R.color.black))
//                .setContentTitle(current.toString())
//                .setContentText("water")
//                .setSubText("Hello")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setSmallIcon(androidx.loader.R.drawable.notification_bg)
//                .setAutoCancel(true)
//        NotificationManagerCompat.from(context).notify(uniqueId, builder.build())
//    }


//    private fun createDefaultChannel() {
//        val context: Context = applicationContext
//
//        val sound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//        val attributes: AudioAttributes = AudioAttributes.Builder()
//            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//            .build()
//
//        val channel = NotificationChannel(
//            CHANNEL_ID1,
//            CHANNEL_NAME1,
//            NotificationManager.IMPORTANCE_HIGH
//        )
//
//        channel.setSound(sound, attributes)
//        NotificationManagerCompat.from(context).createNotificationChannel(channel)
//    }

//    fun multipleReq(){
//        lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
//        var isReadPermissionGranted: Boolean = false
//        var isnotificayioPermissionGranted: Boolean = false
//
//        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//                isReadPermissionGranted = permissions[Manifest.permission.SEND_SMS] ?: isReadPermissionGranted
//                isnotificayioPermissionGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: isReadPermissionGranted
//            }
//    }

    //    fun reqPremission() {
//        isReadPermissionGranted = ContextCompat.checkSelfPermission(this,
//            Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
//        isnotificayioPermissionGranted = ContextCompat.checkSelfPermission(this,
//            Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
//
//        val permissionREq: MutableList<String> = ArrayList()
//        if (!isReadPermissionGranted) {
//            permissionREq.add(Manifest.permission.SEND_SMS)
//        }
//        if (!isReadPermissionGranted) {
//            permissionREq.add(Manifest.permission.POST_NOTIFICATIONS)
//        }
//        if (permissionREq.isNotEmpty()) {
//            permissionLauncher.launch(permissionREq.toTypedArray())
//        }
//    }
}