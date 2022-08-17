package com.matrimonymandaps.vendor.helper


import androidx.appcompat.app.AppCompatActivity
import com.matrimonymandaps.vendor.helper.stringPref

class SharePrefStorage : AppCompatActivity() {



    var fireabseToken: String? by stringPref(Key.SP_FIREBASE_USER_TOKEN_LOGGED)
    var deviceId: String? by stringPref(Key.SP_DEVICE_ID)
    var loggedUserKey: String? by stringPref(Key.SP_LOGGED_USER_KEY)
    var loggedUserPhone: String? by stringPref(Key.SP_LOGGED_USER_PHONE)
    var webviewBaseUrl: String? by stringPref(Key.SP_BASE_URL_TO_LOAD)
    var loginSuccessBoolean: Boolean? by booleanPref(Key.SP_LOGIN_SUCCESS)
    var userId: String? by stringPref(Key.SP_LOGGED_USER_ID)

    var prefLoadNotification: String? by stringPref(Key.PREF_LOAD_NOTIFICATION_URL_IF_AVAILABLE)
    var prefEnableNotification: Int? by intPref(Key.PREF_ENABLE_NOTIFICATIONS_FOR_MI_VIVO)
    var updateVersion: Int? by intPref(Key.SP_LATEST_APP_VERSION_FROM_UPDATE_NOTIFCATION)


    companion object Key {


        const val SP_LOGIN_SUCCESS = "sp_login_succ"
        const val SP_FIREBASE_USER_TOKEN_REFRESHED = "SP_FIREBASE_USER_TOKEN_REFRESHED"
        const val SP_FIREBASE_USER_TOKEN_LOGGED = "SP_FIREBASE_USER_TOKEN_LOGGED"
        const val SP_LATEST_APP_VERSION_FROM_UPDATE_NOTIFCATION = "SP_LATEST_APP_VERSION_FROM_UPDATE_NOTIFCATION"

        const val SP_LOGGED_USER_ID = "SP_LOGGED_USER_ID"
        const val SP_LOGGED_USER_PHONE = "SP_LOGGED_USER_PHONE"
        const val SP_LOGGED_DATE = "SP_LOGGED_DATE"
        const val SP_BASE_URL_TO_LOAD = "SP_BASE_URL_TO_LOAD"
        const val SP_LOGGED_USER_NAME = "SP_LOGGED_USER_NAME"
        const val SP_LOGGED_USER_KEY = "SP_LOGGED_USER_KEY"
        const val SP_DEVICE_ID = "SP_DEIVCE_ID"

        var PREF_LOAD_NOTIFICATION_URL_IF_AVAILABLE = "PREF_LOAD_NOTIFICATION_URL_IF_AVAILABLE"
        var PREF_ENABLE_NOTIFICATIONS_FOR_MI_VIVO = "PREF_ENABLE_NOTIFICATIONS_FOR_MI_VIVO"





        //EnableNotifications
        const val ENABLE_NOTIFICATION_NOT = 0
        const val ENABLE_NOTIFICATION_LATER = 1
        const val ENABLE_NOTIFICATION_OK = 2
    }
}


