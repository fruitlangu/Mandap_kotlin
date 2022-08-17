package com.matrimonymandaps.vendor.data.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyOtpResponse(
        @Json(name = "redirect_url")
        val redirectUrl: String?,
        @Json(name = "key")
        val key: String?,
        @Json(name = "user_id")
        val userId: String?,
        @Json(name = "status")
         val status: String?,
        @Json(name = "msg")
         val msg: String?
)