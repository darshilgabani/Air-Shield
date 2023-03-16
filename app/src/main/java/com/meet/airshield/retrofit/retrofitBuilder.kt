package com.example.unsplashdemo.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object retrofitBuilder {
    val logging = HttpLoggingInterceptor()
    val httpClient = OkHttpClient.Builder()

    val api : ApiService1 by lazy {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClient.addInterceptor(logging)

        Retrofit.Builder()
            .baseUrl("https://api.unsplash.com")
            .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build())
            .build()
            .create(ApiService1::class.java)
    }


    val WeatherApi : ApiService1 by lazy {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClient.addInterceptor(logging)

        Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build())
            .build()
            .create(ApiService1::class.java)
    }

    val WeatherApiWaki : ApiService1 by lazy {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClient.addInterceptor(logging)

        Retrofit.Builder()
            .baseUrl("https://api.waqi.info/")
            .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build())
            .build()
            .create(ApiService1::class.java)
    }
}