package com.matrimonymandaps.vendor.data.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AutoLoginRequest(
        @Json(name = "KEY")
         val kEY: String?,
        @Json(name = "REG_ID")
         val rEGID: String?,
        @Json(name = "IMEINO")
         val iMEINO: String?,
        @Json(name = "USER_ID")
         val userId: String?

)