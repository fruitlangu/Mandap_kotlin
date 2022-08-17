package com.matrimonymandaps.vendor.contract


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

/**
 * Contract for the remote data source that will provide the plug to access network data by obtaining
 * an instance of [IWebService] interface in the implementing class
 * @author Prasan
 * @since 1.0
 * @see IWebService
 */
interface IRemoteDataSource {

    // Webservice Interface that a remote data source impl class needs to provide
    val webService: IWebService

    /**
     * Requests the webservice class to obtain a list of photos by page number
     * @param pageNumber Page Number
     * @return [Flow] of [IOTaskResult] of [PhotoResponse] type
     */
    @ExperimentalCoroutinesApi
    suspend fun setOTPApi(loginRequest: LoginRequest): Flow<IOTaskResult<LoginResponse>>

    @ExperimentalCoroutinesApi
    suspend fun setVerifyOtp(verifyOtpRequest: VerifyOtpRequest): Flow<IOTaskResult<VerifyOtpResponse>>

    @ExperimentalCoroutinesApi
    suspend fun autoLoginApi(autoLoginRequest: AutoLoginRequest): Flow<IOTaskResult<VerifyOtpResponse>>

    @ExperimentalCoroutinesApi
    suspend fun LogoutApi(logoutRequest: LogoutRequest): Flow<IOTaskResult<LogoutResponse>>
}