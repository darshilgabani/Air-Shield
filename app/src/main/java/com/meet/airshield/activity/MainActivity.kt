package com.meet.airshield.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.unsplashdemo.model.WeatherDataClass
import com.example.unsplashdemo.retrofit.retrofitBuilder
import com.meet.airshield.databinding.ActivityMainBinding
import com.meet.airshield.model.WakiModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.mail.*
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    val key1: String = "6e36bca08bf7e7708823224d3772beb7"
    val key2: String = "f2db178aa0985eac450c13303722a0ff15624729"
    var cityRegister: String = ""
    var phoneNumberREgister = ""
    var senderMail = "meet.devstree@gmail.com"
    var senderMailPasword = "warszkyujnnnvews"
    var receiverMail = emailId
    var message = "Air Quality"
    var mail = "Air Quality"
    lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    var isReadPermissionGranted: Boolean = false
    var latitude: Double = 0.00
    var longiude: Double = 0.00


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.progressbar.visibility = View.VISIBLE
        loadData()
        cityRegister=city
        phoneNumberREgister=phoneNumber
        receiverMail=emailId
        getLocation()
        getData()

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isReadPermissionGranted =
                    permissions[Manifest.permission.SEND_SMS] ?: isReadPermissionGranted
            }
        reqPremission()
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

        binding.logOut.setOnClickListener{
            isLogin=false
            isRegistered=false
            phoneNumberREgister=""
            emailId=""
            city=""
            saveData()
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    fun reqPremission() {
        isReadPermissionGranted =
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED

        val permissionREq: MutableList<String> = ArrayList()
        if (!isReadPermissionGranted) {
            permissionREq.add(Manifest.permission.SEND_SMS)
        }
        if (permissionREq.isNotEmpty()) {
            permissionLauncher.launch(permissionREq.toTypedArray())
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

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
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

    fun getLocation() {
        var gc = Geocoder(this, Locale.getDefault())
        var address = gc.getFromLocationName(city, 2)
        latitude = address?.get(0)?.longitude!!
        longiude = address?.get(0)?.longitude!!
    }


    fun getData() {
        var nh3: String? = null
        var no: String? = null
        var no3: String? = null
        var co: String? = null

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
                        co = "CO : ${responseBody.list[0].components?.co}"

                    }
                }

                override fun onFailure(call: Call<WeatherDataClass?>, t: Throwable) {
                    Log.e("openweathermap", t.message.toString())
                    binding.progressbar.visibility = View.INVISIBLE
                }
            })
        Handler().postDelayed({

        retrofitBuilder.WeatherApiWaki.getLocationAQI(cityRegister, key2)
            ?.enqueue(object : Callback<WakiModel?> {
                override fun onResponse(call: Call<WakiModel?>, response: Response<WakiModel?>) {

                    var responseBody = response.body()
//                    if (responseBody?.status=="error"){
//                        Toast.makeText(applicationContext,"Please Register With Other City", Toast.LENGTH_SHORT).show()
//                    }
                    if (responseBody != null) {
                        binding.aqiCircle.aqiTextView.text =
                            responseBody.data?.aqi.toString()

                        var airQuality = AirQualityStatus(responseBody.data?.aqi!!)
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
                                "Sensitive groups: Avoid prolonged or heavy exertion. Move activ\n" +
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
                            var warning =
                                "Air quality is satisfactory, and air pollution poses little or no risk."
                            binding.aqiInfoCard.aqiInfoTitle.text = ""
                            binding.aqiInfoCard.aqiInfoDesc.text = ""
                            binding.attributionCard.attributionTextView.text = precaution
                        }

                        binding.pollutantCard.param2.text =
                            "NO2 : ${responseBody.data?.iaqi?.no2?.v}"
                        binding.pollutantCard.param3.text =
                            "SO2 : ${responseBody.data?.iaqi?.so2?.v}"
                        binding.pollutantCard.param4.text =
                            "PM2.5 : ${responseBody.data?.iaqi?.pm25?.v}"
                        binding.pollutantCard.param5.text =
                            "PM10 : ${responseBody.data?.iaqi?.pm10?.v}"

                        binding.pollutantCard.param1.text = co
                        binding.pollutantCard.param6.text = nh3
                        binding.pollutantCard.param7.text = no
                        binding.pollutantCard.param8.text = no3


                        binding.aqiLocationCard.locationTextView.text =
                            responseBody.data?.city?.name

                        message = "Here is a air quality data" +
                                "\n ** Aqi : ${responseBody.data?.aqi.toString()} **"+
                                "\n ${co}"+
                                "\n NO2 : ${responseBody.data?.iaqi?.no2?.v}"+
                                "\n SO2 : ${responseBody.data?.iaqi?.so2?.v}"+
                                "\n PM 2.5 : ${responseBody.data?.iaqi?.pm25?.v}"+
                                "\n PM 10 : ${responseBody.data?.iaqi?.pm10?.v}"+
                                "\n ${nh3}"+
                                "\n ${no}"+
                                "\n ${no3}"



                        mail = "HERE IS A AIR QUALITY DATA " +
                                "\n" +
                                "\n AQI : ${responseBody.data?.aqi.toString()} " +
                                "\n Level : ${level}" +
                                "HERE IS A AIR QUALITY DATA " +
                                "\n" +
                                "\n ${co}" +
                                "\n NO2  : ${responseBody.data?.iaqi?.no2?.v}" +
                                "\n SO2  : ${responseBody.data?.iaqi?.so2?.v}" +
                                "\n PM 2.5  : ${responseBody.data?.iaqi?.pm25?.v}" +
                                "\n PM 10  : ${responseBody.data?.iaqi?.pm10?.v}" +
                                "\n ${nh3}" +
                                "\n ${no}" +
                                "\n ${no3}" +
                                "\n" +
                                "\n Warning : ${warning}" +
                                "\n Precautions : ${precaution} "
                        Log.e("message", message)
                        binding.progressbar.visibility = View.INVISIBLE
                    }
                }

                override fun onFailure(call: Call<WakiModel?>, t: Throwable) {
                    Log.e("waki", t.message.toString())
                    binding.progressbar.visibility = View.INVISIBLE
                }
            })

        }, 500)
    }


    fun AirQualityStatus(aqi: Int): Int {
        if (aqi <= 50) {
            return 1
        } else if (aqi >= 51 && aqi <= 100) {
            return 2
        } else if (aqi >= 101 && aqi <= 150) {
            return 3
        } else if (aqi >= 151 && aqi <= 200) {
            return 4
        } else if (aqi >= 201 && aqi <= 250) {
            return 5
        } else if (aqi >= 251 && aqi <= 300) {
            return 6
        } else {
            return 0
        }
    }
}