package com.matrimonymandaps.vendor.ui.viewmodel


import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.util.Patterns
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.matrimonymandaps.vendor.BuildConfig
import com.matrimonymandaps.vendor.data.model.request.DEVICEDET
import com.matrimonymandaps.vendor.data.model.request.DT
import com.matrimonymandaps.vendor.data.model.request.LoginRequest
import com.matrimonymandaps.vendor.data.model.request.VerifyOtpRequest
import com.matrimonymandaps.vendor.data.model.response.LoginResponse
import com.matrimonymandaps.vendor.data.model.response.VerifyOtpResponse
import com.matrimonymandaps.vendor.domain.SendOtpApiRequest
import com.matrimonymandaps.vendor.domain.SendVerifyOtpRequest
import com.matrimonymandaps.vendor.helper.*
import com.matrimonymandaps.vendor.ui.view.LoginNavigator
import com.matrimonymandaps.vendor.widget.EditTextPin
import com.squareup.moshi.Json
import dagger.hilt.android.qualifiers.ApplicationContext
import io.fabric.sdk.android.services.common.CommonUtils.hideKeyboard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LoginViewModel @ViewModelInject constructor(@ApplicationContext context: Context, private val sendOtpApiRequest: SendOtpApiRequest, private val sendVerifyOtpRequest: SendVerifyOtpRequest, private val networkHelper: NetworkHelper) : BaseViewModel() {

    val phoneNumber = MutableLiveData<String>("")
    val otpError = MutableLiveData<String>("")
    var isLoading = MutableLiveData<Boolean>()
    var number: String? = null
    var loginNavigator: LoginNavigator? = null
    var context: Context = context
    var isVisible = MutableLiveData<Boolean>(false)
    val closeVisible = MutableLiveData<Boolean>(false)
    val isCursorVisible = MutableLiveData<Boolean>(true)
    val isVisibleSuccess = MutableLiveData<Boolean>(false)
    val isEditTextEnable = MutableLiveData<Boolean>(true)
    var tvPhoneNumberText = MutableLiveData<String>("")
    var tvTimerText = MutableLiveData<String>("")
    var tvResendVisible = MutableLiveData<Boolean>()
    var countDownTimerResendOtp: CountDownTimer? = null


    /**
     * LoginREsponse
     */
    val loginResponse: MutableLiveData<ViewState<LoginResponse>> by lazy {
        MutableLiveData<ViewState<LoginResponse>>()
    }

    /**
     * Verify Otp response
     */
    val verifyOtpResponse: MutableLiveData<ViewState<VerifyOtpResponse>> by lazy {
        MutableLiveData<ViewState<VerifyOtpResponse>>()
    }


    //private var loginRequest =


    val valid = MediatorLiveData<Boolean>().apply {
        addSource(phoneNumber) {
            val valid = isFormValid(it)
            Log.d(it, valid.toString())
            value = valid
        }
    }


    /**
     * Check wether phone number valid or not
     */
    fun isFormValid(phoneNumber: String): Boolean {
        number = phoneNumber
        if (phoneNumber.equals("") || phoneNumber.length < 10) {
            return false
        } else if (Patterns.PHONE.matcher(phoneNumber).matches()) {
            return true
        }
        return false
    }


    /**
     * Submit button for login to get the otp through the api
     */
    @ExperimentalCoroutinesApi
    fun submitBtn() {
        if (networkHelper.isNetworkConnected()) {
            isLoading.value = true
            loginNavigator!!.closeVirtualKeyboard()
            viewModelScope.launch {
                if (networkHelper.isNetworkConnected()) {
                    getViewStateFlowForNetworkCall {
                        sendOtpApiRequest.execute(LoginRequest(number))
                    }.collect {
                        when (it) {

                            is ViewState.Loading -> loginResponse.value = it
                            is ViewState.RenderFailure -> {
                                loginResponse.value = it
                                loginNavigator!!.onerrorMsg(Constants.ERROR_SOMETHING_WENT_WRONG)
                                isLoading.value = false
                            }
                            is ViewState.RenderSuccess<LoginResponse> -> {

                                val msg = it.output.msg
                                val status = it.output.status
                                isLoading.value = false
                                Log.v("output", it.toString());
                                showVerifyOtp(it)
                            }
                        }

                    }
                } else {
                    isLoading.value = false
                    Log.v("output", "no network");
                }
            }
        } else {
            loginNavigator!!.closeVirtualKeyboard()
            loginNavigator!!.onerrorMsg(Constants.ERROR_SOCKET_TIMEOUT_EXCEPTION)
            isLoading.value = false
        }
    }


    /**
     * Number need to change
     */
    fun onClickNumberChange() {
        isVisible.value = false
        closeVisible.value = true
        isEditTextEnable.value = false
        valid.value = false

    }

    /**
     * Clear the number
     */
    fun onClickClearNumber() {
        loginNavigator!!.onclearNumber()
    }


    /**
     * Show verifyotp
     */
    private fun showVerifyOtp(reponse: ViewState.RenderSuccess<LoginResponse>) {
        Log.v("output", "got response");
        isVisible.value = reponse.output.status.equals(Constants.API_STATUS_SUCCESS)
        loginNavigator!!.onLoginResponse(number, reponse.output.msg, reponse.output.status, reponse.output.showResendOption)


    }


    /**
     * Timer will be displaying on otp time
     */
    fun startTimerForResendButton(minutesOfDisableResend: Int) {
        stopTimerForResendButton()
        if (minutesOfDisableResend != 0) {
            tvResendVisible.value = false
            countDownTimerResendOtp = object : CountDownTimer(TimeUnit.MINUTES.toMillis(minutesOfDisableResend.toLong()), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    tvTimerText.value = "Resend OTP button will be enabled in " + millisecondsToTime(millisUntilFinished)

                }

                override fun onFinish() {
                    stopTimerForResendButton()
                }
            }.start()
        }
    }

    /**
     * Stop the time for display the resend button
     */
    fun stopTimerForResendButton() {
        tvTimerText.value = ""
        tvResendVisible.value = true
        if (countDownTimerResendOtp != null) {
            countDownTimerResendOtp!!.cancel()
            countDownTimerResendOtp = null
        }
    }

    /**
     * Millisecond handle
     */
    private fun millisecondsToTime(milliseconds: Long): String? {
        var time = "N/A"
        try {
            val minutes = milliseconds / 1000 / 60
            val seconds = milliseconds / 1000 % 60
            val secondsStr = java.lang.Long.toString(seconds)
            val secs: String
            secs = if (secondsStr.length >= 2) {
                secondsStr.substring(0, 2)
            } else {
                "0$secondsStr"
            }
            time = "$minutes:$secs"
        } catch (ignored: Exception) {
        }
        return time
    }

    /**
     * Otp onclick listener
     */
    fun checkOtpOnlick() {
        if (EditTextPin.onGetOtp().length.equals(6)) {
            EditTextPin.otpError!!.text = ""
            loginNavigator?.verifyOtpOnClick()
        } else {
            Log.v("test", EditTextPin.onGetOtp())
            EditTextPin.otpError!!.text = Constants.ERROR_ENTER_VALID_OTP
        }
    }


    /**
     * Verify Otp request once  submit the phone number for OTP
     */
    @ExperimentalCoroutinesApi
    fun onVerifyOtpRequest(fcmToken: String, deviceId: String?, simOpName: String?, simOp: String?) {
        isLoading.value = true
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                getViewStateFlowForNetworkCall {
                    val verifyOtpRequest: VerifyOtpRequest? = VerifyOtpRequest(
                            number,
                            EditTextPin.onGetOtp(),
                            fcmToken,
                            BuildConfig.VERSION_CODE.toString(),
                            BuildConfig.appType,
                            BuildConfig.VERSION_NAME,
                            deviceId,
                            getLocalIpAddress(),
                            DT(Build.VERSION.SDK_INT.toString(),
                                    DEVICEDET(
                                            simOpName,
                                            Build.PRODUCT,
                                            getAndroidID(context),
                                            getAppVersionName(context),
                                            Build.DEVICE,
                                            Build.MODEL,
                                            simOp,
                                            Build.VERSION.RELEASE,
                                            getAppVersionCode(context).toString(),
                                            Build.SERIAL,
                                            "",
                                            Build.MANUFACTURER,
                                            Build.BRAND,
                                            Build.FINGERPRINT)))

                    showLog("verify", verifyOtpRequest.toString())


                    sendVerifyOtpRequest.execute(verifyOtpRequest!!)
                }.collect {
                    when (it) {


                        is ViewState.Loading -> verifyOtpResponse.value = it
                        is ViewState.RenderFailure -> {
                            loginNavigator!!.onerrorMsg(Constants.ERROR_SOMETHING_WENT_WRONG)
                            isLoading.value = false
                            verifyOtpResponse.value = it
                        }
                        is ViewState.RenderSuccess<VerifyOtpResponse> -> {
                            loginNavigator!!.verifyOtpSuccess(it.output.status.toString(), it.output.msg.toString(), it.output.userId.toString(), it.output.key.toString(), it.output.redirectUrl.toString())

                        }


                    }
                    isLoading.value = false

                }
            } else {
                loginNavigator!!.closeVirtualKeyboard()
                loginNavigator!!.onerrorMsg(Constants.ERROR_SOCKET_TIMEOUT_EXCEPTION)
                isLoading.value = false

                Log.v("output", "no network");
            }
        }
    }


}








