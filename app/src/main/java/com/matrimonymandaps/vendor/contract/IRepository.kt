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
 * The repository class represents the data store of the application. This class is primarily utilised
 * when building offline-first applications where it will make the determination to load the data from
 * a local Room DB vs calling the retrofit function in order to obtain the data
 * @author Prasan
 * @since 1.0
 * @see IRemoteDataSource
 */
interface IRepository {

    val remoteDataSource: IRemoteDataSource

    /**
     * Makes the popular photos API call via data source. In an offline-first architecture, it is at this function
     * call that the Repository class would check if the data for the given page number exists in a Room
     * table, if so return the data from the db, else perform a retrofit call to obtain and store the data
     * into the db before returning the same
     * @param pageNumber Page number of the data called in a paginated data source
     */
    /*@ExperimentalCoroutinesApi
    suspend fun getPhotosByPage(pageNumber: Int): Flow<IOTaskResult<PhotoResponse>>*/

    @ExperimentalCoroutinesApi
    suspend fun setOTPApi(loginRequest: LoginRequest): Flow<IOTaskResult<LoginResponse>>

    @ExperimentalCoroutinesApi
    suspend fun setVerifyOtp(verifyOtpRequest: VerifyOtpRequest): Flow<IOTaskResult<VerifyOtpResponse>>

    @ExperimentalCoroutinesApi
    suspend fun autoLoginApi(autoLoginRequest: AutoLoginRequest): Flow<IOTaskResult<VerifyOtpResponse>>


    @ExperimentalCoroutinesApi
    suspend fun LogoutApi(logoutRequest: LogoutRequest): Flow<IOTaskResult<LogoutResponse>>

}