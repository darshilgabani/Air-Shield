package com.meet.airshield.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.work.*
import com.example.unsplashdemo.model.WeatherDataClass
import com.example.unsplashdemo.retrofit.retrofitBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.meet.airshield.appHelper.AppHelper.emailId
import com.meet.airshield.appHelper.AppHelper.isLogin
import com.meet.airshield.appHelper.AppHelper.isRegistered
import com.meet.airshield.appHelper.AppHelper.loadData
import com.meet.airshield.appHelper.AppHelper.phoneNumber
import com.meet.airshield.appHelper.AppHelper.saveData
import com.meet.airshield.databinding.ActivityMainBinding
import com.meet.airshield.model.WakiModel
import com.meet.airshield.workers.BackgroundWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    private val key1: String = "6e36bca08bf7e7708823224d3772beb7"

    //    val key2: String = "f2db178aa0985eac450c13303722a0ff15624729"
//    var cityRegister: String = ""
    var message = "Air Quality"
    var mail = "Air Quality"
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val key2: String = "f2db178aa0985eac450c13303722a0ff15624729"

        //        const val key3: String = "8caced207cc3cdbf8efc9efa733bf789f09e3d27"
        var apiSendLocation = ""
        var cityRegister: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.progressbar.visibility = View.VISIBLE
        if (!isOnline()) {
            toastMe("Please Turn On Internet")
            return
        }
//        checkGpsStatus()
//        getNotificationPermission()
        getLocationPermission()
        requestOtherPermissions()
        getLastKnownLocation()
        enableGpsBroadcast()
        loadData(this)
        phoneNumberREgister = phoneNumber
        receiverMail = emailId

        binding.aqiCircle.aqiTextView.setOnClickListener {
            if (binding.edtPhoneNumber.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please Enter Contact Number", Toast.LENGTH_SHORT).show()
            } else if (binding.edtPhoneNumber.text!!.length < 10) {
                Toast.makeText(this, "Enter Valid Contract Number", Toast.LENGTH_SHORT).show()
            } else {
                phoneNumber = binding.edtPhoneNumber.text.toString()
                sendSms(message)
                sendMail(mail)
            }
        }

        binding.logOut.setOnClickListener {
            isLogin = false
            isRegistered = false
            phoneNumberREgister = ""
            emailId = ""
            saveData(this)
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
            finish()
        }
        getData()

//        GlobalScope.launch(Dispatchers.Main){
            requestOtherPermissions()
//        }
    }

    fun enableGpsBroadcast() {
        val locationProviderBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    val locationManager =
                        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val isGpsEnabled =
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    val isNetworkEnabled =
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                    if (isGpsEnabled || isNetworkEnabled) {
                        Handler().postDelayed({
                            toastMe("Gps on")
                            getLastKnownLocation()
                            getData()
                        }, 5000)
                    } else {
                        toastMe("Gps is off")
                    }
                }
            }
        }
        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(locationProviderBroadcastReceiver, intentFilter)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }


    fun getACallingBg() {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val data = Data.Builder()
        data.putString("CITY", "geo:$latitude;$longiude")
        val workRequest =
            PeriodicWorkRequest.Builder(
                BackgroundWorker::class.java,
                15,
                TimeUnit.MINUTES,
                1,
                TimeUnit.MINUTES
            ).setInputData(data.build())
                .setConstraints(constraints).build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    fun airQualityStatus(aqi: Int): Int {
        return if (aqi <= 50) {
            1
        } else if (aqi in 51..100) {
            2
        } else if (aqi in 101..150) {
            3
        } else if (aqi in 151..200) {
            4
        } else if (aqi in 201..250) {
            5
        } else if (aqi in 251..300) {
            6
        } else {
            0
        }
    }

    private fun getData() {
        binding.mainLayout.isVisible = false
        var nh3 = ""
        var no = ""
        var no3 = ""
        var co = ""
        var so2 = ""
        var no2 = ""
        var pm25 = ""
        var pm10 = ""

        showProgressDialog()

        retrofitBuilder.WeatherApi.getWeatherData(latitude.toString(), longiude.toString(), key1)
            .enqueue(object : Callback<WeatherDataClass?> {
                override fun onResponse(
                    call: Call<WeatherDataClass?>,
                    response: Response<WeatherDataClass?>,
                ) {
                    var responseBody = response.body()
                    if (responseBody != null) {
                        nh3 = "NH3 : ${responseBody.list[0].components?.nh3}"
                        no = "NO : ${responseBody.list[0].components?.no}"
                        no3 = "NO3 : ${responseBody.list[0].components?.o3}"
                        so2 = "So2 : ${responseBody.list[0].components?.so2}"

                    }
                }

                override fun onFailure(call: Call<WeatherDataClass?>, t: Throwable) {
                    Log.e("openweathermap", t.message.toString())
                    binding.progressbar.visibility = View.INVISIBLE
                }
            })
        Handler().postDelayed({
            retrofitBuilder.WeatherApiWaki.getLocationAQI(apiSendLocation, key2)
                ?.enqueue(object : Callback<WakiModel?> {
                    override fun onResponse(
                        call: Call<WakiModel?>,
                        response: Response<WakiModel?>
                    ) {
                        hideProgressDialog()
                        val responseBody = response.body()
//                    if (responseBody?.status=="error"){
//                        Toast.makeText(applicationContext,"Please Register With Other City", Toast.LENGTH_SHORT).show()
//                    }
                        if (responseBody != null) {

                            if (responseBody.data?.aqi == null) {
                                binding.errorLayout.isVisible = true
                                binding.mainLayout.isVisible = false
                                return
                            }
                            binding.aqiCircle.aqiTextView.text =
                                responseBody.data?.aqi.toString()
                            val airQuality = airQualityStatus(responseBody.data?.aqi!!)
//                        var airQuality = 4
                            var level = ""
                            var warning = ""
                            var precaution = ""

                            if (airQuality == 1) {
                                level = "Good"
                                warning =
                                    "Air quality is satisfactory, and air pollution poses little or no risk."
                                precaution = "It's Great Day to be Active Outside"
                                binding.aqiInfoCard.aqiInfoTitle.text = level
                                binding.aqiInfoCard.aqiInfoDesc.text = warning
                            } else if (airQuality == 2) {
                                level = "Moderate"
                                warning =
                                    "Air quality is acceptable. However, there may be a risk for some people, particularly those who are unusually sensitive to air pollution."
                                precaution =
                                    "Unusually sensitive people: Consider reducing prolonged or heavy exertion. Watch for symptoms such as coughing or shortness of breath. These are signs to take it easier.\n" +
                                            "\n" +
                                            "Everyone else: It's a good day to be active outside."
                                binding.aqiInfoCard.aqiInfoTitle.text = level
                                binding.aqiInfoCard.aqiInfoDesc.text = warning
                                binding.attributionCard.attributionTextView.text = precaution
                            } else if (airQuality == 3) {
                                level = "Unhealthy for Sensitive Groups"
                                warning =
                                    "Members of sensitive groups may experience health effects. The general public is less likely to be affected."
                                precaution =
                                    "Sensitive groups: Reduce prolonged or heavy exertion. It's OK to be\n" +
                                            "\n" +
                                            "What Should I Do?\n" +
                                            "\n" +
                                            "active outside, but take more breaks and do less intense activities. Watch for symptoms such as coughing or shortness of breath. People with asthma should follow their asthma action plans and keep quick relief medicine handy.\n" +
                                            "\n" +
                                            "If you have heart disease: Symptoms such as palpitations, shortness\n" +
                                            "\n" +
                                            "of breath, or unusual fatigue may indicate a serious problemn. If you\n" +
                                            "\n" +
                                            "have any of these, contact your heath care provider."
                                binding.aqiInfoCard.aqiInfoTitle.text = level
                                binding.aqiInfoCard.aqiInfoDesc.text = warning
                                binding.attributionCard.attributionTextView.text = precaution
                            } else if (airQuality == 4) {
                                level = "Unhealthy"
                                warning =
                                    "Some members of the general public may experience health effects; members of sensitive groups may experience more serious health effects."
                                precaution =
                                    "Sensitive groups: Avoid prolonged or heavy exertion.\n" +
                                            "\n" +
                                            "indoors or reschedule to a time when the air quality is better.\n" +
                                            "\n" +
                                            "Everyone else: Reduce prolonged or heavy exertion. Take more\n" +
                                            "\n" +
                                            "breaks during all outdoor activities."
                                binding.aqiInfoCard.aqiInfoTitle.text = level
                                binding.aqiInfoCard.aqiInfoDesc.text = warning
                                binding.attributionCard.attributionTextView.text = precaution
                            } else if (airQuality == 5) {
                                level = "Very Unhealthy"
                                warning =
                                    "Health alert: The risk of health effects is increased for everyone."
                                precaution =
                                    "Sensitive groups: Avoid all physical activity outdoors. Move activities\n" +
                                            "\n" +
                                            "indoors or reschedule to a time when air quality is better.\n" +
                                            "\n" +
                                            "Everyone else: Avoid prolonged or heavy exertion. Consider moving activities indoors or rescheduling to a time when air quality is better."
                                binding.aqiInfoCard.aqiInfoTitle.text = level
                                binding.aqiInfoCard.aqiInfoDesc.text = warning
                                binding.attributionCard.attributionTextView.text = precaution
                            } else if (airQuality == 6) {
                                level = "Hazardous"
                                warning =
                                    "Health warning of emergency conditions: everyone is more likely to be affected."
                                precaution = "\n" +
                                        "\n" +
                                        "Everyone: Avoid all physical activity outdoors. Sensitive groups: Remain indoors and keep activity levels low.\n" +
                                        "\n" +
                                        "Follow tips for keeping particle levels low indoors."
                                binding.aqiInfoCard.aqiInfoTitle.text = level
                                binding.aqiInfoCard.aqiInfoDesc.text = warning
                                binding.attributionCard.attributionTextView.text = precaution
                            } else {
                                level = "Good"
                                precaution = ""
                                warning =
                                    "Air quality is satisfactory, and air pollution poses little or no risk."
                                binding.aqiInfoCard.aqiInfoTitle.text = ""
                                binding.aqiInfoCard.aqiInfoDesc.text = ""
                                binding.attributionCard.attributionTextView.text = precaution
                            }

                            co = "CO : ${responseBody.data?.iaqi?.co?.v}"
                            no2 = "NO2 : ${responseBody.data?.iaqi?.no2?.v}"
                            pm25 = "PM2.5 : ${responseBody.data?.iaqi?.pm25?.v}"
                            pm10 = "PM10 : ${responseBody.data?.iaqi?.pm10?.v}"

                            binding.pollutantCard.param1.text = co
                            binding.pollutantCard.param2.text = no2
                            binding.pollutantCard.param3.text = so2
                            binding.pollutantCard.param4.text = pm25
                            binding.pollutantCard.param5.text = pm10

                            binding.pollutantCard.param6.text = nh3
                            binding.pollutantCard.param7.text = no
                            binding.pollutantCard.param8.text = no3


                            binding.aqiLocationCard.locationTextView.text =
                                responseBody.data?.city?.name

                            message = "Here is a air quality data" +
                                    "\n ** Aqi : ${responseBody.data?.aqi.toString()} **" +
                                    "\n $co" +
                                    "\n $no2" +
                                    "\n $so2" +
                                    "\n $pm25" +
                                    "\n $pm10" +
                                    "\n $nh3" +
                                    "\n $no" +
                                    "\n $no3"



                            mail = "HERE IS A AIR QUALITY DATA " +
                                    "\n" +
                                    "\n AQI : ${responseBody.data?.aqi.toString()} " +
                                    "\n Level : $level" + "\n" +
                                    "HERE IS A AIR QUALITY DATA " +
                                    "\n" +
                                    "\n $co" +
                                    "\n $no2" +
                                    "\n $no3" +
                                    "\n $pm25" +
                                    "\n $pm10" +
                                    "\n $nh3" +
                                    "\n $no" +
                                    "\n $no3" +
                                    "\n" +
                                    "\n Warning : $warning" +
                                    "\n Precautions : $precaution"
                            Log.e("message", message)
                            binding.mainLayout.isVisible = true
                            binding.progressbar.visibility = View.INVISIBLE
                            getACallingBg()
                        }
                    }

                    override fun onFailure(call: Call<WakiModel?>, t: Throwable) {
                        Log.e("waqi", t.message.toString())
                        binding.mainLayout.isVisible = true
                        binding.errorLayout.isVisible = false
                        binding.progressbar.visibility = View.INVISIBLE
                    }
                })

        }, 500)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //added for crash while getting location permission
        Handler(Looper.getMainLooper()).postDelayed({
            getLastKnownLocation()
            getData()
        }, 1200)
    }

    fun getLocationPermission() {
        val REQUEST_LOCATION_PERMISSION = 1
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.e("lat", latitude.toString())
                    Log.e("long", longitude.toString())
                    MainActivity.apiSendLocation = "geo:$latitude;$longitude"
                }
            }
    }

}