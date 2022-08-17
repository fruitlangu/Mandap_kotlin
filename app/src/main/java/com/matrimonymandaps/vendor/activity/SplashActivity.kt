package com.matrimonymandaps.vendor.activity

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.matrimonymandaps.vendor.R
import com.matrimonymandaps.vendor.data.model.request.AutoLoginRequest
import com.matrimonymandaps.vendor.data.model.request.LogoutRequest
import com.matrimonymandaps.vendor.data.model.response.LogoutResponse
import com.matrimonymandaps.vendor.data.model.response.VerifyOtpResponse
import com.matrimonymandaps.vendor.domain.SendAutoLoginRequest
import com.matrimonymandaps.vendor.domain.SendLogoutRequest
import com.matrimonymandaps.vendor.helper.*
import com.matrimonymandaps.vendor.ui.view.LoginActivity
import com.matrimonymandaps.vendor.ui.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    var sharePrefStorage: SharePrefStorage = SharePrefStorage()
    var loginBool: Boolean? = false
    var fcmToken: String? = ""
    var deviceId: String? = ""
    var loggedUserPhone: String? = ""
    var loggedUserKey: String? = ""
    var loggedUserId: String? = ""
    var latestAppVersionFromUpdateNotification: Int?=0
    var progressBar: ProgressBar? = null
    var notificationURl:String?=""
    var isPausedForPlayStore:Boolean? = false
     var wentToSettingsPage:Boolean? = false

    @Inject
    lateinit var firebaseInstanceId: FirebaseInstanceId

    @Inject
    lateinit var autoLoginRequest: SendAutoLoginRequest

    @Inject
    lateinit var sendLogoutRequest: SendLogoutRequest

    @Inject
    lateinit var networkHelper: NetworkHelper

    /**
     * LoginREsponse
     */
    val autoLoginResponse: MutableLiveData<ViewState<VerifyOtpResponse>> by lazy {
        MutableLiveData<ViewState<VerifyOtpResponse>>()
    }

    /**
     * LoginREsponse
     */
    val LogoutResponse: MutableLiveData<ViewState<LogoutResponse>> by lazy {
        MutableLiveData<ViewState<LogoutResponse>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        PrefDelegate.init(this)
        loginBool = sharePrefStorage.loginSuccessBoolean
        fcmToken = sharePrefStorage.fireabseToken
        deviceId = sharePrefStorage.deviceId
        progressBar = findViewById(R.id.progress_circular)
        progressBar?.visibility = View.GONE
        notificationURl=intent.getStringExtra(Constants.INTENT_LOAD_NOTIFICATION_URL)
        if (notificationURl == null) {
            notificationURl = ""
        }
        sharePrefStorage.prefLoadNotification=notificationURl
        checkPermission()
        firebaseStorage()
    }


    /**
     * Go to next activity
     */
    @ExperimentalCoroutinesApi
    private fun nextScreen() {
        latestAppVersionFromUpdateNotification= sharePrefStorage.updateVersion!!.toInt()
        if (latestAppVersionFromUpdateNotification!! > getAppVersionCode(this)) {
            showAppUpdateAvailableDialog()
            return
        }

        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                goToNextActivity()
            }, 1000)
        }

    }


    /**
     * UpdateDialog
     */
    private fun showAppUpdateAvailableDialog() {
        val updateAlertDialog: AlertDialog = AlertDialog.Builder(this)
                .setTitle(Constants.APP_UPDATE_TITLE)
                .setMessage(Constants.APP_UPDATE_MESSAGE)
                .setCancelable(false)
                .setPositiveButton(Constants.APP_UPDATE_POSITIVE_BUTTON, DialogInterface.OnClickListener { dialogInterface, i ->
                    isPausedForPlayStore = true
                    goToAppPlaystore(this)
                })
                .setNegativeButton(Constants.APP_UPDATE_NEGATIVE_BUTTON, DialogInterface.OnClickListener { dialogInterface, i -> finish() })
                .setOnKeyListener(object : DialogInterface.OnKeyListener {
                    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent): Boolean {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() === KeyEvent.ACTION_UP) finish()
                        return false
                    }
                }).create()
        if (!(this as Activity).isFinishing) {
            updateAlertDialog.show()
            updateAlertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.alertDialogNegativeButtonColor))
        }
    }



    /**
     * Go to next activity
     */
    @ExperimentalCoroutinesApi
    fun goToNextActivity() {
        if (!networkHelper.isNetworkConnected()) {
            if (!this.isFinishing) {
                val alertDialog = AlertDialog.Builder(this)
                        .setMessage("Please switch ON your internet connection & try again")
                        .setCancelable(false)
                        .setPositiveButton("Retry") { dialogInterface, i -> goToNextActivity() }
                        .create()
                alertDialog.show()
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.negColor))
            } else {
                showToast(this@SplashActivity.getString(R.string.network_error))
            }
            return
        }
        if (loginBool!!) {
            loggedUserPhone = sharePrefStorage.loggedUserPhone
            loggedUserKey = sharePrefStorage.loggedUserKey
            loggedUserId = sharePrefStorage.userId
            if (!loggedUserPhone.equals("") && !loggedUserKey.equals("")) {
                callAutoLoginApi(loggedUserPhone, loggedUserKey, loggedUserId)
            } else {
                goToLoginActivity()
            }
        } else {
            goToLoginActivity()
        }

    }


    /**
     * AutoLoginApi
     */
    @ExperimentalCoroutinesApi
    private fun callAutoLoginApi(loggedUserPhone: String?, loggedUserKey: String?, loggedUserId: String?) {
        progressBar?.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.Main) {
            if (networkHelper.isNetworkConnected()) {
                getViewStateFlowForNetworkCall {
                    autoLoginRequest.execute(AutoLoginRequest(loggedUserKey, fcmToken, deviceId, loggedUserId))

                }.collect {
                    when (it) {
                        is ViewState.Loading -> autoLoginResponse.value = it
                        is ViewState.RenderFailure -> {
                            autoLoginResponse.value = it
                            progressBar?.visibility = View.GONE
                            if(!networkHelper.isNetworkConnected()) {
                                showToast(this@SplashActivity.getString(R.string.network_error))
                            }else{
                                showToast(Constants.ERROR_SOMETHING_WENT_WRONG)
                            }
                            goToLoginActivity()
                        }
                        is ViewState.RenderSuccess<VerifyOtpResponse> -> {
                            val msg = it.output.msg
                            val status = it.output.status
                            progressBar?.visibility = View.GONE
                            Log.v("output", it.toString())
                            if (status.equals(Constants.API_STATUS_SUCCESS)) {
                                sharePrefStorage.fireabseToken = fcmToken
                                sharePrefStorage.loggedUserKey = it.output.key
                                sharePrefStorage.userId = it.output.userId
                                sharePrefStorage.loginSuccessBoolean = true
                                sharePrefStorage.loggedUserPhone = loggedUserPhone
                                sharePrefStorage.webviewBaseUrl = it.output.redirectUrl
                                if (sharePrefStorage.fireabseToken != null && sharePrefStorage.loggedUserKey != null && sharePrefStorage.userId != null &&
                                        sharePrefStorage.loginSuccessBoolean != null && sharePrefStorage.loginSuccessBoolean != false && sharePrefStorage.webviewBaseUrl != null) {
                                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                    finish()
                                } else {
                                    logoutApi(it.output.msg)
                                }
                            }else{
                                logoutApi(it.output.msg)
                            }

                        }
                    }
                }
            } else {
                progressBar?.visibility = View.GONE
                Log.v("output", "no network");
            }
        }
    }


    /**
     *
     *If login has failed it will directly go to logout api
     */
    @ExperimentalCoroutinesApi
    private fun logoutApi(autoFailureMessage: String?) {
        if (networkHelper.isNetworkConnected()) {
            GlobalScope.launch(Dispatchers.Main) {
                if (networkHelper.isNetworkConnected()) {
                    getViewStateFlowForNetworkCall {
                        sendLogoutRequest.execute(LogoutRequest(loggedUserKey, fcmToken, deviceId))
                    }.collect {
                        when (it) {
                            is ViewState.Loading -> LogoutResponse.value = it
                            is ViewState.RenderFailure ->{
                                LogoutResponse.value = it
                                if(!networkHelper.isNetworkConnected()) {
                                    showToast(this@SplashActivity.getString(R.string.network_error))
                                }else{
                                    showToast(Constants.ERROR_SOMETHING_WENT_WRONG)
                                }
                            }
                            is ViewState.RenderSuccess<LogoutResponse> -> {
                                val msg = it.output.msg
                                val status = it.output.status
                                progressBar?.visibility = View.GONE
                                Log.v("output", it.toString())
                                if (status.equals(Constants.API_STATUS_SUCCESS)) {
                                    showToast(autoFailureMessage!!)
                                    clearSavedCredentials(this@SplashActivity)
                                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Redirect to login activity
     */
    private fun goToLoginActivity() {
        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        finish()
    }

    /**
     * Store the fcm and token
     */
    private fun firebaseStorage() {
        if (sharePrefStorage.deviceId == null) {
            firebaseInstanceId.instanceId.addOnSuccessListener(OnSuccessListener { instantceIdResult ->
                sharePrefStorage.deviceId = instantceIdResult.id
            })
        }

    }

    /**
     * Check the permission
     */

    private fun checkPermission() {
        Dexter.withContext(this@SplashActivity)
                .withPermissions(
                        Manifest.permission.READ_CONTACTS
                        , Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_CONTACTS)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                //toast("OK")
                                nextScreen()
                            } else {
                                showDeniedPermissionNeededDialog()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permissions: MutableList<PermissionRequest>?,
                            token: PermissionToken?
                    ) {
                        // Remember to invoke this method when the custom rationale is closed
                        // or just by default if you don't want to use any custom rationale.
                        token?.continuePermissionRequest()
                    }
                })
                .withErrorListener {
                    showToast(it.name)
                    showDeniedPermissionNeededDialog()
                }
                .check()
    }

    override fun onResume() {
        super.onResume()
        if (isPausedForPlayStore!!) {
            if (latestAppVersionFromUpdateNotification!! > getAppVersionCode(this)) {
                isPausedForPlayStore = true
                showAppUpdateAvailableDialog()
            }else {
                isPausedForPlayStore = false
                goToNextActivity()
            }

        } else if (wentToSettingsPage!!) {
            wentToSettingsPage = false
            requestingPermissions()

        }
    }


    /**
     * Show denied dialog
     */
    fun showDeniedPermissionNeededDialog() {
        wentToSettingsPage=true
        AlertDialog.Builder(this)
                .setMessage("You need to allow permissions to enter into the app")
                .setPositiveButton("Allow") { dialogInterface, i -> requestingPermissions() }
                .setNegativeButton("Cancel") { dialogInterface, i -> finish() }
                .setCancelable(false).create().show()
    }


    /**
     * Request Permission again
     */
    fun requestingPermissions() {
        checkPermission()
    }
}