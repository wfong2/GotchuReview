package com.gotchureviews.app.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gotchureviews.app.R
import com.gotchureviews.app.ui.components.RatingRow
import com.gotchureviews.app.ui.components.ReviewCard
import com.gotchureviews.app.ui.components.StarRating
import com.gotchureviews.app.util.formatCurrency
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ContractorDetailScreen(
    contractorId: String,
    viewModel: ContractorDetailViewModel = hiltViewModel(),
) {
    val contractor by viewModel.contractor.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(contractorId) {
        viewModel.load(contractorId)
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val c = contractor ?: run {
        errorMessage?.let { error ->
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Header
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = c.businessName.ifEmpty { c.name },
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "${c.category.replaceFirstChar { it.uppercase() }} · ${c.locationDisplay} ${c.zipCode}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Ratings
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StarRating(rating = c.ratingOverall, starSize = 18.dp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = String.format("%.1f", c.ratingOverall),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.contractor_overall),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            RatingRow(stringResource(R.string.rating_quality), c.ratingQuality)
            RatingRow(stringResource(R.string.rating_communication), c.ratingCommunication)
            RatingRow(stringResource(R.string.rating_timeliness), c.ratingTimeliness)
            RatingRow(stringResource(R.string.rating_value), c.ratingValue)

            Text(
                text = "${c.reviewCount} ${stringResource(R.string.contractor_verified_reviews)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        HorizontalDivider()

        // Pricing
        if (c.pricingMedian > 0) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.contractor_pricing),
                    style = MaterialTheme.typography.titleMedium,
                )

                PricingRow(
                    label = stringResource(R.string.contractor_typical_job),
                    value = c.pricingMedian.formatCurrency(),
                )

                if (c.pricingLaborMedian > 0) {
                    PricingRow(
                        label = stringResource(R.string.contractor_labor),
                        value = "${c.pricingLaborMedian.formatCurrency()} (${(c.pricingLaborRatio * 100).toInt()}%)",
                    )
                    PricingRow(
                        label = stringResource(R.string.contractor_materials),
                        value = "${c.pricingMaterialsMedian.formatCurrency()} (${((1 - c.pricingLaborRatio) * 100).toInt()}%)",
                    )
                }

                PricingRow(
                    label = stringResource(R.string.contractor_range),
                    value = c.priceRangeDisplay,
                )

                Text(
                    text = stringResource(R.string.contractor_based_on, c.reviewCount, c.zipCode),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            HorizontalDivider()
        }

        // Reviews
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.contractor_reviews),
                style = MaterialTheme.typography.titleMedium,
            )
            reviews.forEach { review ->
                ReviewCard(review = review)
            }
        }
    }
}

@Composable
private fun PricingRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
