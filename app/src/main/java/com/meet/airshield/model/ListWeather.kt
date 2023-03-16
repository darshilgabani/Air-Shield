package com.example.unsplashdemo.model


import com.google.gson.annotations.SerializedName


data class ListWeather(
    @SerializedName("main") var main: Main? = Main(),
    @SerializedName("components") var components: Components? = Components(),
    @SerializedName("dt") var dt: Int? = null,
)