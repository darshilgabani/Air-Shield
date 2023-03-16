package com.example.unsplashdemo.retrofit

import com.example.unsplashdemo.Utils.Constants.END_POINT_WEather
import com.example.unsplashdemo.model.WeatherDataClass
import com.meet.airshield.model.WakiModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService1 {



    @GET(END_POINT_WEather)
    fun getWeatherData(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
    ): Call<WeatherDataClass>


    @GET("feed/{geo}/")
    fun getLocationAQI(@Path("geo") geo: String?, @Query("token") token: String?): Call<WakiModel?>?
}