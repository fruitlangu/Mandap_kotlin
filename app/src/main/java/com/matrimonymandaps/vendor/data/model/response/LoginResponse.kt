package com.matrimonymandaps.vendor.data.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
        @Json(name = "show_resend_option")
        val showResendOption: Int?,
        @Json(name = "status")
        val status: String?,
        @Json(name = "msg")
        val msg: String?
)