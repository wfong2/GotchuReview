package com.gotchureviews.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: String,
    val contractorId: String,
    val userId: String,
    val ratingQuality: Int,
    val ratingCommunication: Int,
    val ratingTimeliness: Int,
    val ratingValue: Int,
    val overallRating: Double,
    val title: String,
    val body: String,
    val workType: String,
    val isVerified: Boolean,
    val invoiceId: String? = null,
    val createdAt: String,
    val invoice: InvoiceSummary? = null,
)

@Serializable
data class InvoiceSummary(
    val totalAmount: Double,
    val laborCost: Double? = null,
    val materialsCost: Double? = null,
    val description: String,
    val invoiceDate: String? = null,
    val source: String,
)

@Serializable
data class ReviewBrief(
    val id: String,
    val overallRating: Double,
    val title: String,
)
