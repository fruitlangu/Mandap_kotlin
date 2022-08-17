package com.matrimonymandaps.vendor.network



import com.matrimonymandaps.vendor.data.model.request.AutoLoginRequest
import com.matrimonymandaps.vendor.data.model.request.LoginRequest
import com.matrimonymandaps.vendor.data.model.request.LogoutRequest
import com.matrimonymandaps.vendor.data.model.request.VerifyOtpRequest
import com.matrimonymandaps.vendor.data.model.response.LoginResponse
import com.matrimonymandaps.vendor.data.model.response.LogoutResponse
import com.matrimonymandaps.vendor.data.model.response.VerifyOtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Singleton

/**
 * Retrofit API class for the 500px API
 * @author Prasan
 * @since 1.0
 */
@Singleton
interface FiveHundredPixelsAPI {

    /**
     * Performs a GET call to obtain a paginated list of photos
     * @param key API Key
     * @param feature feature source the photos should come from
     * @param page Page number of the data where the photos should come from
     * @return [Response] instance of [PhotoResponse] type
     */
    @POST("api/vendor/send-otp")
    suspend fun sendOtpApi(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/vendor/verify-otp")
    suspend fun verifyOtpApi(@Body verifyOtpRequest: VerifyOtpRequest ):Response<VerifyOtpResponse>

    @POST("api/vendor/auto-login")
    suspend fun autoLoginApi(@Body autoLoginRequest: AutoLoginRequest ):Response<VerifyOtpResponse>

    @POST("api/vendor/app-logout")
    suspend fun logoutapi(@Body logoutRequest: LogoutRequest ):Response<LogoutResponse>

    //NOTE: As per https://github.com/500px/legacy-api-documentation/blob/master/basics/formats_and_terms.md#image-urls-and-image-sizes
    // image_size=5,6 should return a array of Images with variable sizes, but in the response only 1 size is being obtained

}