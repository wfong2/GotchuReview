package com.gotchureviews.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppUser(
    val id: String,
    val googleEmail: String,
    val displayName: String,
    val firebaseUid: String,
    val creditBalance: Int,
    val notifyNewReview: Boolean,
    val notifyPriceUpdate: Boolean,
    val notifyInvoiceProcessed: Boolean,
    val notifyDraftReminder: Boolean,
    val prefZipCode: String,
    val prefCity: String,
    val prefState: String,
    val prefCountry: String,
    val fcmToken: String? = null,
    val createdAt: String,
    val updatedAt: String,
)
