package com.matrimonymandaps.vendor.domain

import com.matrimonymandaps.vendor.contract.IRepository
import com.matrimonymandaps.vendor.contract.IUseCase
import com.matrimonymandaps.vendor.data.model.request.LoginRequest
import com.matrimonymandaps.vendor.data.model.request.VerifyOtpRequest
import com.matrimonymandaps.vendor.data.model.response.LoginResponse
import com.matrimonymandaps.vendor.helper.IOTaskResult

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [IUseCase] class implementation that retrieves a paginated list of photos from the service
 * Takes a page number as input and returns the [IOTaskResult] [PhotoResponse] instance in return
 * @author Prasan
 * @since 1.0
 */
@Singleton
class SendOtpApiRequest @Inject constructor(override val repository: IRepository) :
        IUseCase<LoginRequest, LoginResponse> {

    @ExperimentalCoroutinesApi
    override suspend fun execute(input: LoginRequest): Flow<IOTaskResult<LoginResponse>> =
        repository.setOTPApi(input)


}