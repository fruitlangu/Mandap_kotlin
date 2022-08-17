package com.matrimonymandaps.vendor.data.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyOtpRequest (
    @Json(name ="MOBILE" )
     val mOBILE: String? ,


    @Json(name ="OTP" )
     val oTP: String? ,


    @Json(name ="REG_ID" )
     val rEGID: String? ,


    @Json(name ="VC" )
     val vC: String? ,


    @Json(name ="T" )
     val t: String? ,

    @Json(name ="V" )
     val v: String?,


    @Json(name ="IMEINO" )
     val iMEINO: String? ,


    @Json(name ="IP" )
     val iP: String? ,


    @Json(name ="DT" )
     val dT: DT?



)