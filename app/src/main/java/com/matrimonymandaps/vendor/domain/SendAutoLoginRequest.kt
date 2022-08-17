package com.matrimonymandaps.vendor.domain

import com.matrimonymandaps.vendor.contract.IRepository
import com.matrimonymandaps.vendor.contract.IUseCase
import com.matrimonymandaps.vendor.data.model.request.AutoLoginRequest
import com.matrimonymandaps.vendor.data.model.request.LogoutRequest
import com.matrimonymandaps.vendor.data.model.response.LogoutResponse
import com.matrimonymandaps.vendor.data.model.response.VerifyOtpResponse
import com.matrimonymandaps.vendor.helper.IOTaskResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendAutoLoginRequest @Inject constructor(override val repository: IRepository) :
        IUseCase<AutoLoginRequest, VerifyOtpResponse> {

    @ExperimentalCoroutinesApi
    override suspend fun execute(input: AutoLoginRequest): Flow<IOTaskResult<VerifyOtpResponse>> =
            repository.autoLoginApi(input)




}