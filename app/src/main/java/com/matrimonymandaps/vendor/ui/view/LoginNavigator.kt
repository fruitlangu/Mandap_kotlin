package com.matrimonymandaps.vendor.ui.view

import android.content.SharedPreferences

interface LoginNavigator {
    fun onLoginClick()
    fun onLoginResponse(phoneNumer:String?,message: String?,status:String?,showResendoption:Int?)
    fun onForgotClick()
    fun onclearNumber()
    fun closeVirtualKeyboard()
    fun onerrorMsg(errorMsg:String)
    fun verifyOtpOnClick()
    fun verifyOtpSuccess(Status: String, msg: String, userId: String, key: String, redirectUrl: String)
}