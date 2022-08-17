package com.matrimonymandaps.vendor.ui.view

import android.app.Dialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.webkit.URLUtil
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.matrimonymandaps.vendor.R
import com.matrimonymandaps.vendor.activity.SplashActivity
import com.matrimonymandaps.vendor.databinding.ActivityMainBinding
import com.matrimonymandaps.vendor.helper.Constants
import com.matrimonymandaps.vendor.helper.Constants.Key.WEBVIEW_GO_BACK_ON_RETRY
import com.matrimonymandaps.vendor.helper.PrefDelegate
import com.matrimonymandaps.vendor.helper.SharePrefStorage
import com.matrimonymandaps.vendor.helper.clearSavedCredentials
import com.matrimonymandaps.vendor.ui.base.BaseActivity
import com.matrimonymandaps.vendor.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.matrimonymandaps.vendor.ui.viewmodel.MainViewModel.mainNavigator as mainNavigator1

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity(), MainNavigator {
    private val mainViewModel: MainViewModel by viewModels()
    private var webUrl: String? = ""
    var sharedPref: SharePrefStorage = SharePrefStorage()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PrefDelegate.init(this)
        val activityMainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        activityMainBinding.setLifecycleOwner(this)
        activityMainBinding.mainViewModel = mainViewModel
        mainViewModel.webViewUrl.value = sharedPref.webviewBaseUrl
        mainNavigator1.mainNavigator = this
        mainViewModel.fcmToken = sharedPref.fireabseToken
        mainViewModel.deviceId = sharedPref.deviceId
        mainViewModel.loggedUserId = sharedPref.userId
        mainViewModel.loggedUserKey = sharedPref.loggedUserKey
        mainViewModel.loggedUserPhone = sharedPref.loggedUserPhone
        oninit()
        //mainViewModel.showWebView(activityMainBinding.webviewDashboard)
    }


    /**
     * Init and cheeck the permission for notification
     */
    fun oninit() {
        if (sharedPref.webviewBaseUrl == null) {
            showToast(Constants.ERROR_SOMETHING_WENT_WRONG)
            startActivity(Intent(this@MainActivity, SplashActivity::class.java))
            finish()
        }

        //Enable notification for MI /VIVO/ OPPO
        val manufacturer = Build.MANUFACTURER
        if ("xiaomi".equals(manufacturer, ignoreCase = true) || "oppo".equals(manufacturer, ignoreCase = true) || "vivo".equals(manufacturer, ignoreCase = true)) {
            if (sharedPref.prefEnableNotification == Constants.ENABLE_NOTIFICATION_NOT) {
                enableNotification()
            }
        }

        if (sharedPref.prefLoadNotification != "" && URLUtil.isNetworkUrl(sharedPref.prefLoadNotification)) {
            sharedPref.prefLoadNotification = ""
            Handler().postDelayed({
                try {
                    if (!this.isFinishing()) {
                        mainViewModel.webViewUrl.value = sharedPref.prefLoadNotification
                    }
                } catch (ignored: java.lang.Exception) {
                }
            }, 3000)
        }
    }


    /**
     * Enable the notification
     */
    private fun enableNotification() {
        val appname = getString(R.string.app_name)
        val dialog = Dialog(this@MainActivity, R.style.EnableNotification)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_enable_notification)
        val tvPermissionContent = dialog.findViewById<View>(R.id.tv_notification_permission_content) as TextView
        val btnOK = dialog.findViewById<View>(R.id.btn_ok) as Button
        val btnLater = dialog.findViewById<View>(R.id.btn_later) as Button
        tvPermissionContent.text = Html.fromHtml("To receive any notifications from $appname app; please switch it on under Autostart setting.")
        btnOK.setOnClickListener {
            dialog.cancel()
            goToAutostartEnableNotification()
        }
        btnLater.setOnClickListener {
            sharedPref.prefEnableNotification = Constants.ENABLE_NOTIFICATION_LATER
            dialog.cancel()
        }
        dialog.show()
    }

    /**
     * Auto Start enable notification
     */
    private fun goToAutostartEnableNotification() {
        sharedPref.prefEnableNotification = Constants.ENABLE_NOTIFICATION_OK
        try {
            val intent = Intent()
            val manufacturer = Build.MANUFACTURER
            if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
            } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")
            } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.iqoo.secure", "com.iqoo.secure.MainGuideActivity.")
            }
            val list: List<ResolveInfo> = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (list.size > 0) {
                startActivity(intent)
            }
        } catch (ignored: Exception) {
        }
    }

    override fun visibleStatus(visbleBoolean: Boolean) {
        // mainViewModel.progressVisible.value = visbleBoolean
        mainViewModel.progressVisible.postValue(visbleBoolean)
    }

    override fun canGoBack(canGoBack: Boolean) {
        mainViewModel.canGoBack.postValue(canGoBack)
    }

    override fun checkNetworkConnection(): Boolean {
        if(mainViewModel.checkNetworkConnection()){
            return true
        }
        return false
    }

    override fun response(errorResponse: String) {
        if (errorResponse.equals("logout")) {
            if (mainViewModel.checkNetworkConnection()) {
                val alertDialog = AlertDialog.Builder(this)
                        .setMessage("Are you sure, you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Logout") { dialogInterface, i ->
                            dialogInterface.dismiss()
                            mainViewModel.callLogoutApi(Constants.ERROR_SOMETHING_WENT_WRONG)
                        }
                        .setNegativeButton("No", null)
                        .create()
                alertDialog.show()
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.negColor))
            }
        } else if (errorResponse.equals(Constants.API_STATUS_SUCCESS)) {
            clearSavedCredentials(this@MainActivity)
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        } else {
            showToast(errorResponse)
        }
    }

    /**
     * Check the internet connection with snackbar
     */
    fun noInternetConnectionSnackbar(retryUrl: String) {
        showSnackBarWithRetry("Please switch ON your internet connection", retryUrl)
    }

    /**
     * Show the snack bar
     */
    private fun showSnackBarWithRetry(snackbarMessage: String, retryUrl: String) {
        val noInternetConnectionSnackbar = Snackbar.make(findViewById(R.id.main_layout), snackbarMessage, Snackbar.LENGTH_INDEFINITE)
        noInternetConnectionSnackbar.setAction("Retry") {
            noInternetConnectionSnackbar.dismiss()
            if (mainViewModel.checkNetworkConnection()) {
                if (retryUrl == WEBVIEW_GO_BACK_ON_RETRY) {
                    mainViewModel.goBack.value = true
                } else {
                    mainViewModel.webViewUrl.postValue(retryUrl)
                }
            } else {
                showSnackBarWithRetry(snackbarMessage, retryUrl)
            }
        }.show()
    }

    override fun reload(url: String?) {
        if(url.equals("")) {
            noInternetConnectionSnackbar(WEBVIEW_GO_BACK_ON_RETRY)
        }else{
            val intents = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intents)
        }


    }

    override fun webUrl(): String {
      return   sharedPref.webviewBaseUrl!!
    }

    override fun onBackPressed() {
        if (mainViewModel.canGoBack.value == true) {
            if (!mainViewModel.checkNetworkConnection()) noInternetConnectionSnackbar(WEBVIEW_GO_BACK_ON_RETRY) else mainViewModel.goBack.value = true
        } else {
            super.onBackPressed()
        }
    }




}