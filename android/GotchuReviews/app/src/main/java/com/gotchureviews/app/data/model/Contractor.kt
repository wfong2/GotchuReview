package com.gotchureviews.app.data.model

import kotlinx.serialization.Serializable
import java.text.NumberFormat
import java.util.Locale

@Serializable
data class Contractor(
    val id: String,
    val name: String,
    val businessName: String,
    val category: String,
    val phone: String,
    val email: String,
    val website: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val ratingOverall: Double,
    val ratingQuality: Double,
    val ratingCommunication: Double,
    val ratingTimeliness: Double,
    val ratingValue: Double,
    val reviewCount: Int,
    val pricingCurrency: String,
    val pricingMin: Double,
    val pricingMax: Double,
    val pricingMedian: Double,
    val pricingLaborMedian: Double,
    val pricingMaterialsMedian: Double,
    val pricingLaborRatio: Double,
) {
    val locationDisplay: String
        get() = listOf(city, state).filter { it.isNotEmpty() }.joinToString(", ")

    val priceRangeDisplay: String
        get() {
            if (pricingMin <= 0 || pricingMax <= 0) return ""
            val fmt = NumberFormat.getIntegerInstance(Locale.US)
            return "$${fmt.format(pricingMin.toLong())} – $${fmt.format(pricingMax.toLong())}"
        }
}

@Serializable
data class ContractorMatch(
    val contractor: Contractor,
    val confidence: Int,
)

@Serializable
data class ContractorBrief(
    val id: String,
    val name: String,
    val businessName: String,
    val category: String,
    val city: String? = null,
    val state: String? = null,
)
