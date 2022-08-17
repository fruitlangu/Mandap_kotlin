package com.matrimonymandaps.vendor.network



import com.matrimonymandaps.vendor.contract.IWebService
import com.matrimonymandaps.vendor.data.model.request.AutoLoginRequest
import com.matrimonymandaps.vendor.data.model.request.LoginRequest
import com.matrimonymandaps.vendor.data.model.request.LogoutRequest
import com.matrimonymandaps.vendor.data.model.request.VerifyOtpRequest
import com.matrimonymandaps.vendor.data.model.response.LoginResponse
import com.matrimonymandaps.vendor.data.model.response.LogoutResponse
import com.matrimonymandaps.vendor.data.model.response.VerifyOtpResponse
import com.matrimonymandaps.vendor.helper.IOTaskResult
import com.matrimonymandaps.vendor.helper.performSafeNetworkApiCall
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [IWebService] impl class which uses Retrofit to provide the app with the functionality to make
 * network requests
 * @author Prasan
 * @since 1.0
 * @see FiveHundredPixelsAPI
 * @see [IWebService]
 */
@Singleton
class RetrofitWebService @Inject constructor(private val retrofitClient: FiveHundredPixelsAPI) :
        IWebService {

    @ExperimentalCoroutinesApi
    override suspend fun setOTPApi(
        loginRequest: LoginRequest
    ): Flow<IOTaskResult<LoginResponse>> =

        performSafeNetworkApiCall("Error Obtaining Photos") {
            retrofitClient.sendOtpApi(
                loginRequest = loginRequest
            )
        }

    @ExperimentalCoroutinesApi
    override suspend fun setVerifyOtp(verifyOtpRequest: VerifyOtpRequest): Flow<IOTaskResult<VerifyOtpResponse>> =

            performSafeNetworkApiCall("Error Obtaining Photos") {
                retrofitClient.verifyOtpApi(
                        verifyOtpRequest = verifyOtpRequest
                )
            }

    @ExperimentalCoroutinesApi
    override suspend fun autoLoginApi(autoLoginRequest: AutoLoginRequest): Flow<IOTaskResult<VerifyOtpResponse>> =
        performSafeNetworkApiCall("Error Obtaining Photos") {
            retrofitClient.autoLoginApi(
                    autoLoginRequest = autoLoginRequest
            )
        }


    @ExperimentalCoroutinesApi
    override suspend fun LogoutApi(logoutRequest: LogoutRequest): Flow<IOTaskResult<LogoutResponse>> =
        performSafeNetworkApiCall("Error Obtaining Photos") {
            retrofitClient.logoutapi(
                    logoutRequest = logoutRequest
            )
        }



}