package com.gotchureviews.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VendorTemplate(
    val vendorNameNormalized: String,
    val fieldLabels: List<String>,
    val sectionOrder: List<String>,
    val invoiceNumberFormat: String,
    val logoPosition: String,
    val fontCategory: String,
    val dominantColors: List<String>,
    val tableStyle: String,
)

@Serializable
data class ExtractedInvoice(
    val contractorName: String,
    val businessName: String,
    val totalAmount: Double,
    val currency: String,
    val laborCost: Double? = null,
    val materialsCost: Double? = null,
    val hourlyRate: Double? = null,
    val projectDuration: String? = null,
    val invoiceDate: String? = null,
    val description: String,
    val lineItems: List<LineItem>,
    val contractorPhone: String? = null,
    val contractorEmail: String? = null,
    val contractorAddress: String? = null,
    val zipCode: String? = null,
    val vendorTemplate: VendorTemplate? = null,
)

@Serializable
data class LineItem(
    val description: String,
    val amount: Double,
    val category: String,
    val quantity: Int = 1,
    private val unitPrice: Double? = null,
) {
    val effectiveUnitPrice: Double get() = unitPrice ?: amount
}

@Serializable
data class ExtractionResponse(
    val extracted: ExtractedInvoice,
    val documentHash: String,
    val vendorFingerprint: String? = null,
    val vendorTemplate: VendorTemplate? = null,
    val contractorMatches: List<ContractorMatch>,
    val estimatedBreakdown: Boolean,
)

@Serializable
data class DetailedInvoice(
    val id: String,
    val documentHash: String,
    val totalAmount: Double,
    val currency: String,
    val laborCost: Double? = null,
    val materialsCost: Double? = null,
    val hourlyRate: Double? = null,
    val projectDuration: String? = null,
    val invoiceDate: String? = null,
    val description: String,
    val lineItems: List<LineItem>,
    val createdAt: String,
    val contractor: ContractorBrief,
    val review: ReviewBrief? = null,
)
