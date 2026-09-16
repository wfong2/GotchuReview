package com.gotchureviews.app.data.remote.dto

import com.gotchureviews.app.data.model.Contractor
import com.gotchureviews.app.data.model.Review
import kotlinx.serialization.Serializable

@Serializable
data class ContractorListResponse(
    val contractors: List<Contractor>,
    val total: Int,
)

@Serializable
data class ContractorDetailResponse(
    val contractor: Contractor,
    val reviews: List<Review>,
)
