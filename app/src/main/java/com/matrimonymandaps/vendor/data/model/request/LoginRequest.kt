package com.matrimonymandaps.vendor.data.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(

    @Json(name = "Mobile")
    val mobile: String?

)