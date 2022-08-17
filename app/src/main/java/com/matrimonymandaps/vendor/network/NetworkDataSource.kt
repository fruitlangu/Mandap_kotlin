package com.matrimonymandaps.vendor.network


import com.matrimonymandaps.vendor.contract.IRemoteDataSource
import com.matrimonymandaps.vendor.contract.IWebService
import com.matrimonymandaps.vendor.data.model.request.AutoLoginRequest
import com.matrimonymandaps.vendor.data.model.request.LoginRequest
import com.matrimonymandaps.vendor.data.model.request.LogoutRequest
import com.matrimonymandaps.vendor.data.model.request.VerifyOtpRequest
import com.matrimonymandaps.vendor.data.model.response.LoginResponse
import com.matrimonymandaps.vendor.data.model.response.LogoutResponse
import com.matrimonymandaps.vendor.data.model.response.VerifyOtpResponse
import com.matrimonymandaps.vendor.helper.IOTaskResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [IRemoteDataSource] impl class that provides access to network API calls
 * @author Prasan
 * @since 1.0
 * @see IRemoteDataSource
 * @see IWebService
 */
@Singleton
class NetworkDataSource @Inject constructor(override val webService: IWebService) :
        IRemoteDataSource {

    @ExperimentalCoroutinesApi
    override suspend fun setOTPApi(loginRequest: LoginRequest): Flow<IOTaskResult<LoginResponse>> =
        webService.setOTPApi(loginRequest)

    @ExperimentalCoroutinesApi
    override suspend fun setVerifyOtp(verifyOtpRequest: VerifyOtpRequest): Flow<IOTaskResult<VerifyOtpResponse>> =webService.setVerifyOtp(verifyOtpRequest)

    @ExperimentalCoroutinesApi
    override suspend fun autoLoginApi(autoLoginRequest: AutoLoginRequest): Flow<IOTaskResult<VerifyOtpResponse>> =
       webService.autoLoginApi(autoLoginRequest)


    @ExperimentalCoroutinesApi
    override suspend fun LogoutApi(logoutRequest: LogoutRequest): Flow<IOTaskResult<LogoutResponse>> =
        webService.LogoutApi(logoutRequest)



}