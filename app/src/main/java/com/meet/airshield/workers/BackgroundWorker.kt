package com.meet.airshield.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.unsplashdemo.retrofit.retrofitBuilder
import com.meet.airshield.R
import com.meet.airshield.activity.MainActivity
import com.meet.airshield.model.WakiModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BackgroundWorker(private val context: Context, params: WorkerParameters) :
    Worker(context, params) {
    var uniqueId = 123
    private var CHANNEL_ID1 = "id"
    private var CHANNEL_NAME1 = "name"
    override fun doWork(): Result {
        val city =  inputData.getString("CITY")
        getData(city)
        createDefaultChannel()
        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(
        aqiLevel: String,
        level: String,
        precaution: String,
        warning: String
    ) {

        val builder1 = NotificationCompat.Builder(applicationContext, CHANNEL_ID1).setStyle(
            NotificationCompat.BigTextStyle().setBigContentTitle(aqiLevel)
                .bigText(precaution)
        ).setContentTitle(aqiLevel).setSubText(warning)
            .setOnlyAlertOnce(true)
            .setContentText(level)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
        NotificationManagerCompat.from(context).notify(uniqueId, builder1.build())
    }

    private fun createDefaultChannel() {
        val context: Context = applicationContext

        val sound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val attributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val channel = NotificationChannel(
            CHANNEL_ID1,
            CHANNEL_NAME1,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.setSound(sound, attributes)
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }

    fun getData(city: String?) {
        var aqiLevel = 100
        var level = ""
        var warning = ""
        var precaution = ""
        retrofitBuilder.WeatherApiWaki.getLocationAQI(
            city,
            MainActivity.key2
        )
            ?.enqueue(object : Callback<WakiModel?> {
                override fun onResponse(
                    call: Call<WakiModel?>,
                    response: Response<WakiModel?>
                ) {
                    if (response.body() != null) {
                        val responseBody = response.body()

                        if (responseBody?.data?.aqi == null) return
                        val airQuality = airQualityStatus(responseBody.data?.aqi!!.toInt())
                        aqiLevel = responseBody.data?.aqi!!

                        if (airQuality == 1) {
                            level = "Good"
                            warning =
                                "Air quality is satisfactory, and air pollution poses little or no risk."
                            precaution = "It's Great Day to be Active Outside"
                        } else if (airQuality == 2) {
                            level = "Moderate"
                            warning =
                                "Air quality is acceptable. However, there may be a risk for some people, particularly those who are unusually sensitive to air pollution."
                            precaution =
                                "Unusually sensitive people: Consider reducing prolonged or heavy exertion. Watch for symptoms such as coughing or shortness of breath. These are signs to take it easier.\n" +
                                        "\n" +
                                        "Everyone else: It's a good day to be active outside."
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
                        } else if (airQuality == 4) {
                            level = "Unhealthy"
                            warning = "Some members of the general public may experience health effects; members of sensitive groups may experience more serious health effects."
                            precaution = "Sensitive groups: Avoid prolonged or heavy exertion\n" +
                                    "\n" +
                                    "indoors or reschedule to a time when the air quality is better.\n" +
                                    "\n" +
                                    "Everyone else: Reduce prolonged or heavy exertion. Take more\n" +
                                    "\n" +
                                    "breaks during all outdoor activities."
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
                        } else if (airQuality == 6) {
                            level = "Hazardous"
                            warning = "Health warning of emergency conditions: everyone is more likely to be affected."
                            precaution = "\n" +
                                    "\n" +
                                    "Everyone: Avoid all physical activity outdoors. Sensitive groups: Remain indoors and keep activity levels low.\n" +
                                    "\n" +
                                    "Follow tips for keeping particle levels low indoors."

                        } else {
                            level = "Good"
                            precaution = "hello"
                            warning = "Air quality is satisfactory, and air pollution poses little or no risk."

                        }

                    }
                    sendNotification(aqiLevel.toString(), level, precaution,warning)
                }

                override fun onFailure(call: Call<WakiModel?>, t: Throwable) {
                    Log.e("waki", t.message.toString())
                }
            })


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

}