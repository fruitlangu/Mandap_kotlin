package com.matrimonymandaps.vendor.domain

import com.matrimonymandaps.vendor.contract.IRepository
import com.matrimonymandaps.vendor.contract.IUseCase
import com.matrimonymandaps.vendor.data.model.request.LoginRequest
import com.matrimonymandaps.vendor.data.model.request.LogoutRequest
import com.matrimonymandaps.vendor.data.model.response.LoginResponse
import com.matrimonymandaps.vendor.data.model.response.LogoutResponse
import com.matrimonymandaps.vendor.data.model.response.VerifyOtpResponse
import com.matrimonymandaps.vendor.helper.IOTaskResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendLogoutRequest @Inject constructor(override val repository: IRepository) :
IUseCase<LogoutRequest, LogoutResponse> {

    @ExperimentalCoroutinesApi
    override suspend fun execute(input: LogoutRequest): Flow<IOTaskResult<LogoutResponse>> =
            repository.LogoutApi(input)




}