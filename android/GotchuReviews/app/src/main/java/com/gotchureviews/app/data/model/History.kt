package com.gotchureviews.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VendorHistoryResponse(
    val vendors: List<VendorGroup>,
    val summary: HistorySummary,
)

@Serializable
data class VendorGroup(
    val contractor: ContractorBrief,
    val invoiceCount: Int,
    val totalSpent: Double,
    val latestInvoiceDate: String,
    val invoices: List<DetailedInvoice>,
) {
    val id: String get() = contractor.id
}

@Serializable
data class HistorySummary(
    val totalInvoices: Int,
    val totalSpent: Double,
    val contractorCount: Int,
)
