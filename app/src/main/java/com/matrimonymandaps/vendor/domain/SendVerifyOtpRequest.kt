package com.matrimonymandaps.vendor.domain

import com.matrimonymandaps.vendor.contract.IRepository
import com.matrimonymandaps.vendor.contract.IUseCase
import com.matrimonymandaps.vendor.data.model.request.LoginRequest
import com.matrimonymandaps.vendor.data.model.request.VerifyOtpRequest
import com.matrimonymandaps.vendor.data.model.response.LoginResponse
import com.matrimonymandaps.vendor.data.model.response.VerifyOtpResponse
import com.matrimonymandaps.vendor.helper.IOTaskResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendVerifyOtpRequest @Inject constructor(override val repository: IRepository):IUseCase<VerifyOtpRequest,VerifyOtpResponse> {
    @ExperimentalCoroutinesApi
    override suspend fun execute(input: VerifyOtpRequest): Flow<IOTaskResult<VerifyOtpResponse>> =  repository.setVerifyOtp(input)
}