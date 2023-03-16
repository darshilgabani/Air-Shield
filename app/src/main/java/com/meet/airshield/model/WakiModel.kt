package com.meet.airshield.model

import com.google.gson.annotations.SerializedName


data class WakiModel(
    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: Data? = Data(),
)

data class Attributions(

    @SerializedName("url") var url: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("logo") var logo: String? = null,

    )

data class City(

    @SerializedName("geo") var geo: ArrayList<Double> = arrayListOf(),
    @SerializedName("name") var name: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("location") var location: String? = null,

    )

data class Co(

    @SerializedName("v") var v: Double? = null,

    )

data class Dew(

    @SerializedName("v") var v: Double? = null,

    )

data class H(

    @SerializedName("v") var v: Double? = null,

    )

data class No2(

    @SerializedName("v") var v: Double? = null,

    )

data class O3(

    @SerializedName("v") var v: Double? = null,

    )

data class P(

    @SerializedName("v") var v: Double? = null,

    )

data class Pm10(

    @SerializedName("v") var v: Double? = null,

    )


data class Pm25(

    @SerializedName("v") var v: Double? = null,

    )

data class So2(

    @SerializedName("v") var v: Double? = null,

    )

data class T(

    @SerializedName("v") var v: Double? = null,

    )

data class W(

    @SerializedName("v") var v: Double? = null,

    )


data class Iaqi(

    @SerializedName("co") var co: Co? = Co(),
    @SerializedName("dew") var dew: Dew? = Dew(),
    @SerializedName("h") var h: H? = H(),
    @SerializedName("no2") var no2: No2? = No2(),
    @SerializedName("o3") var o3: O3? = O3(),
    @SerializedName("p") var p: P? = P(),
    @SerializedName("pm10") var pm10: Pm10? = Pm10(),
    @SerializedName("pm25") var pm25: Pm25? = Pm25(),
    @SerializedName("so2") var so2: So2? = So2(),
    @SerializedName("t") var t: T? = T(),
    @SerializedName("w") var w: W? = W(),

    )

//data class Time (
//
//    @SerializedName("s"   ) var s   : String? = null,
//    @SerializedName("tz"  ) var tz  : String? = null,
//    @SerializedName("v"   ) var v   : Int?    = null,
//    @SerializedName("iso" ) var iso : String? = null
//
//)

data class Data(

    @SerializedName("aqi")
    var aqi: Int? = null,
//    @SerializedName("idx"          ) var idx          : Int?                    = null,
//    @SerializedName("attributions" ) var attributions : ArrayList<Attributions> = arrayListOf(),
    @SerializedName("city") var city: City? = City(),
    @SerializedName("dominentpol") var dominentpol: String? = null,
    @SerializedName("iaqi") var iaqi: Iaqi? = Iaqi(),
//    @SerializedName("time"         ) var time         : Time?                   = Time(),
//    @SerializedName("forecast"     ) var forecast     : Forecast?               = Forecast(),
//    @SerializedName("debug"        ) var debug        : Debug?                  = Debug()

)