package com.meet.airshield.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


open class BaseActivity : AppCompatActivity() {
    var isLogin: Boolean = false
    var isRegistered: Boolean = false
    var phoneNumber: String = ""
    var password: String = ""
    var emailId: String = ""
    var city: String = ""
    private var fusedLocationClient: FusedLocationProviderClient? = null
    var latitude: Double = 0.00
    var longiude: Double = 0.00
    private var alertDialog: AlertDialog? = null
    private lateinit var locationManager: LocationManager
    var gpsStatus = false

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
    fun isLocationGPSEnable(): Boolean {

//        if (LocationHelper.getLocationUpdateByUser()) return true

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            showAlertMessage(
                "",
                "gps is not enabled",
                false,
               "yes",
                "no"
            ) {
                if (it) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            }
            return false
        } else {
            return true
        }
    }

    open fun showAlertMessage(
        title: String = "",
        str: String,
        isCancelable: Boolean,
        positiveText: String,
        nagetiveText: String,
        callback: (isPositive: Boolean) -> Unit,
    ): AlertDialog? {
        try {
            if (alertDialog != null && alertDialog!!.isShowing) {
                alertDialog!!.dismiss()
            }
            val builder = AlertDialog.Builder(this)
                .setMessage(str)
                .setCancelable(isCancelable)
                .setPositiveButton(positiveText.takeIf { positiveText.isNotBlank() }
                    ?:"ok") { _, _ -> callback.invoke(true) }
                .setNegativeButton(nagetiveText.takeIf { nagetiveText.isNotBlank() }
                    ?:"cancel") { _, _ -> callback.invoke(false) }

            if (title.isNotBlank()) builder.setTitle(title)

            alertDialog = builder.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return alertDialog
    }
    fun fetchLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val task = fusedLocationClient?.lastLocation

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
        task?.addOnSuccessListener {
            if (it != null) {
               latitude=it.latitude
                longiude=it.longitude
                Log.e("latitude",latitude.toString())
                Log.e("longitude",longiude.toString())

            }
        }
    }

}