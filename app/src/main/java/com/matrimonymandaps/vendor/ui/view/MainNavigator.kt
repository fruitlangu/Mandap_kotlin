package com.matrimonymandaps.vendor.ui.view

interface MainNavigator {
    fun visibleStatus(visbleBoolean: Boolean)
    fun canGoBack(canGoBack: Boolean)
    fun checkNetworkConnection(): Boolean
    fun response(errorResponse: String)
    fun reload(url: String?)
    fun webUrl(): String
}