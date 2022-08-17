package com.matrimonymandaps.vendor.ui.view

import android.Manifest
import android.content.Context
import android.content.Intent

import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.matrimonymandaps.vendor.R
import com.matrimonymandaps.vendor.databinding.ActivityLoginBinding
import com.matrimonymandaps.vendor.helper.Constants
import com.matrimonymandaps.vendor.helper.PrefDelegate
import com.matrimonymandaps.vendor.helper.SharePrefStorage
import com.matrimonymandaps.vendor.helper.handleRetrofitFailure
import com.matrimonymandaps.vendor.ui.base.BaseActivity
import com.matrimonymandaps.vendor.ui.viewmodel.LoginViewModel
import com.matrimonymandaps.vendor.widget.EditTextPin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity: BaseActivity(),LoginNavigator {

    private val loginViewModel : LoginViewModel by viewModels()
    var sharePrefStorage:SharePrefStorage=SharePrefStorage()
    var simOpName:String?=""
    var simOp:String?=""
    @Inject
    lateinit var firebaseInstanceId: FirebaseInstanceId


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PrefDelegate.init(this)
        val activityMainBinding =  DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        activityMainBinding.setLifecycleOwner(this)
        activityMainBinding.loginViewModel=loginViewModel
        activityMainBinding.loginViewModel?.loginNavigator=this
    }

    override fun onLoginClick() {
        TODO("Not yet implemented")
    }

    override fun onLoginResponse(number: String?,message: String?, status: String?, showResendoption: Int?) {
        if(status.equals(Constants.API_STATUS_SUCCESS)) {
            val phoneTxt: String? = getColoredSpanned(number, "#000000")
            val phoneTxtNumber: String? = getColoredSpanned(resources.getString(R.string.otp_ques), "#6A6A6A")
            loginViewModel.tvPhoneNumberText.value = HtmlCompat.fromHtml(phoneTxt + " " + phoneTxtNumber, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            loginViewModel.isCursorVisible.value=false
            //loginViewModel.isVisible.value=true
            EditTextPin.onClear()
            showKeyboard(EditTextPin.etOTP1!!)
            if(showResendoption!=null && showResendoption!=0) {
                loginViewModel.startTimerForResendButton(showResendoption)
            }else{
                loginViewModel.startTimerForResendButton(1)
            }
        }else{
            showSnackBar(findViewById(R.id.constraint_root),message!!);
        }
    }

    private fun getColoredSpanned(text: String?, color: String?): String {
        return "<font color=$color>$text</font>"
    }

    override fun onForgotClick() {
        TODO("Not yet implemented")
    }

    override fun onclearNumber() {
        AlertDialog.Builder(this)
                .setTitle("OTP sent. Do you want to change the mobile number?")
                .setPositiveButton("No", null)
                .setNegativeButton("Yes") { dialogInterface, i ->  loginViewModel.isCursorVisible.value=true
                    loginViewModel.isEditTextEnable.value=true
                    loginViewModel.valid.value=true

                }.create().show()
    }

    override fun closeVirtualKeyboard() {
        hideKeyboard()
    }

    override fun onerrorMsg(errorMsg:String) {
        handleRetrofitFailure(findViewById(R.id.constraint_root),errorMsg)
    }


    @ExperimentalCoroutinesApi
    override fun verifyOtpOnClick() {

        if(sharePrefStorage.fireabseToken ==null|| sharePrefStorage.deviceId==null){
            firebaseInstanceId.instanceId.addOnSuccessListener ( OnSuccessListener { instanceIdResult ->
                sharePrefStorage.fireabseToken = firebaseInstanceId.getToken()
                sharePrefStorage.deviceId = firebaseInstanceId.id
                if(sharePrefStorage.fireabseToken!=null){
                    verifyOtpOnClick()
                }else{
                    showSnackBar(findViewById(R.id.constraint_root),"Can't get FCM Token. Try again later ");
                }
            })
            return
        }else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                simOpName = "PhonePermissionDenied"
                simOp = "PhonePermissionDenied"
            } else {
                val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (telephonyManager != null) {
                    simOpName = telephonyManager.simOperator
                    simOp = telephonyManager.networkOperatorName
                } else {
                    simOpName = ""
                    simOp = ""
                }
            }

            loginViewModel.onVerifyOtpRequest(sharePrefStorage.fireabseToken.toString(), sharePrefStorage.deviceId.toString(), simOpName, simOp)
        }
    }



    override fun verifyOtpSuccess(status: String, msg: String, userId: String, key: String, redirectUrl: String) {
        if(status.equals(Constants.API_STATUS_SUCCESS)){
            loginViewModel.isVisibleSuccess.value=true
            loginViewModel.isVisible.value=false
            sharePrefStorage.loggedUserKey=key
            sharePrefStorage.fireabseToken=sharePrefStorage.fireabseToken
            sharePrefStorage.loggedUserPhone=loginViewModel.number
            sharePrefStorage.webviewBaseUrl=redirectUrl
            sharePrefStorage.loginSuccessBoolean=true
            sharePrefStorage.userId=userId

            if(sharePrefStorage.loggedUserKey!=null&&sharePrefStorage.fireabseToken!=null&&sharePrefStorage.loggedUserPhone!=null && sharePrefStorage.webviewBaseUrl!=null
                    && sharePrefStorage.loginSuccessBoolean!=false){
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }else{
                showSnackBar(findViewById(R.id.constraint_root), Constants.ERROR_AUTO_LOGIN_API_FAILURE)
            }
        }else {
            loginViewModel.isVisibleSuccess.value=false
            showSnackBar(findViewById(R.id.constraint_root), msg)
        }
    }



}