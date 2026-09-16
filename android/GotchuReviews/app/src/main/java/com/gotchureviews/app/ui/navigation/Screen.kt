package com.gotchureviews.app.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Main : Screen

    // Explore tab
    @Serializable
    data object Explore : Screen

    @Serializable
    data class ContractorDetail(val contractorId: String) : Screen

    // History tab
    @Serializable
    data object History : Screen

    @Serializable
    data class VendorInvoiceList(val vendorIndex: Int) : Screen

    @Serializable
    data class InvoiceDetail(val vendorIndex: Int, val invoiceIndex: Int) : Screen

    @Serializable
    data class FullImage(val documentHash: String) : Screen
}
