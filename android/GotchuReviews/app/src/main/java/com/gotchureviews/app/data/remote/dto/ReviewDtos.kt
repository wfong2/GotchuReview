package com.gotchureviews.app.data.remote.dto

import com.gotchureviews.app.data.model.LineItem
import com.gotchureviews.app.data.model.Review
import kotlinx.serialization.Serializable

@Serializable
data class ReviewSubmission(
    val contractorId: String? = null,
    val newContractor: NewContractor? = null,
    val invoiceData: InvoiceData,
    val documentHash: String,
    val ratings: Ratings,
    val title: String,
    val body: String,
    val workType: String,
    val estimatedBreakdown: Boolean,
)

@Serializable
data class NewContractor(
    val name: String,
    val businessName: String? = null,
    val category: String,
    val phone: String? = null,
    val email: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
)

@Serializable
data class InvoiceData(
    val totalAmount: Double,
    val currency: String,
    val laborCost: Double? = null,
    val materialsCost: Double? = null,
    val hourlyRate: Double? = null,
    val projectDuration: String? = null,
    val invoiceDate: String? = null,
    val description: String,
    val zipCode: String? = null,
    val lineItems: List<LineItem>,
)

@Serializable
data class Ratings(
    val quality: Int,
    val communication: Int,
    val timeliness: Int,
    val value: Int,
)

@Serializable
data class ReviewResponse(
    val review: Review,
    val creditBalance: Int,
)
