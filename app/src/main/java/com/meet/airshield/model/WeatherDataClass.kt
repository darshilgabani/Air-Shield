package com.example.unsplashdemo.model


import com.google.gson.annotations.SerializedName

data class WeatherDataClass(
    @SerializedName("coord" ) var coord : Coord?          = Coord(),
    @SerializedName("list"  ) var list  : ArrayList<ListWeather> = arrayListOf()
)