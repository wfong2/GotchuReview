package com.gotchureviews.app.data.repository

import com.gotchureviews.app.data.model.AppUser
import com.gotchureviews.app.data.model.VendorHistoryResponse
import com.gotchureviews.app.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getCurrentUser(): AppUser {
        return apiService.getCurrentUser().user
    }

    suspend fun getVendorHistory(): VendorHistoryResponse {
        return apiService.getVendorHistory()
    }

    suspend fun getCreditBalance(): Int {
        return apiService.getCreditBalance().balance
    }
}
