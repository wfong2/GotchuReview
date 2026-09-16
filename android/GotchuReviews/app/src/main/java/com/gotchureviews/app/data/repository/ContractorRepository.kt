package com.gotchureviews.app.data.repository

import com.gotchureviews.app.data.remote.ApiService
import com.gotchureviews.app.data.remote.dto.ContractorDetailResponse
import com.gotchureviews.app.data.remote.dto.ContractorListResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContractorRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun search(
        query: String? = null,
        category: String? = null,
        zipCode: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): ContractorListResponse {
        return apiService.searchContractors(
            query = query?.takeIf { it.isNotBlank() },
            category = category,
            zipCode = zipCode?.takeIf { it.isNotBlank() },
            limit = limit,
            offset = offset,
        )
    }

    suspend fun getDetail(id: String): ContractorDetailResponse {
        return apiService.getContractor(id)
    }
}
