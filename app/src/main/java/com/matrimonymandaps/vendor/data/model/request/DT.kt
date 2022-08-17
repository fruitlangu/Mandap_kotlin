package com.matrimonymandaps.vendor.data.model.request

import com.squareup.moshi.Json


data class DT(
        @Json(name = "BUILDVERSION")
        private var bUILDVERSION: String? = null,

        @Json(name = "DEVICEDET")
        private val dEVICEDET:DEVICEDET?
)

data class DEVICEDET(
        @Json(name = "SIM_OP_NAME")
        val sIMOPNAME: String?,


        @Json(name = "PRODUCT")
        val pRODUCT: String?,


        @Json(name = "DEVICEID")
        val dEVICEID: String?,


        @Json(name = "APP_VERSION_NAME")
        val aPPVERSIONNAME: String?,


        @Json(name = "DEVICE")
        val dEVICE: String?,


        @Json(name = "MODEL")
        val mODEL: String?,


        @Json(name = "OP_NAME")
        val oPNAME: String?,


        @Json(name = "RELEASE")
        val rELEASE: String?,


        @Json(name = "APP_VERSION")
        val aPPVERSION: String?,


        @Json(name = "SERIAL")
        val sERIAL: String?,


        @Json(name = "LINE_NUMBER")
        val lINENUMBER: String?,


        @Json(name = "MANUFACTURER")
        val mANUFACTURER: String?,


        @Json(name = "BRAND")
        val bRAND: String?,


        @Json(name = "DEVICEFINGERPRINT")
        val dEVICEFINGERPRINT: String?
)