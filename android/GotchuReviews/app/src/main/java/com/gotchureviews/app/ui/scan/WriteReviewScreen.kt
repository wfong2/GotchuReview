package com.gotchureviews.app.ui.scan

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gotchureviews.app.R
import com.gotchureviews.app.ui.components.InteractiveStarRating
import com.gotchureviews.app.ui.theme.Green

@Composable
fun WriteReviewScreen(
    viewModel: ScanViewModel,
    modifier: Modifier = Modifier,
) {
    val extractionResult by viewModel.extractionResult.collectAsState()
    val ratingQuality by viewModel.ratingQuality.collectAsState()
    val ratingCommunication by viewModel.ratingCommunication.collectAsState()
    val ratingTimeliness by viewModel.ratingTimeliness.collectAsState()
    val ratingValue by viewModel.ratingValue.collectAsState()
    val reviewTitle by viewModel.reviewTitle.collectAsState()
    val reviewBody by viewModel.reviewBody.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    val result = extractionResult ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "${stringResource(R.string.review_reviewing)} ${result.extracted.contractorName}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "${stringResource(R.string.review_work)} ${result.extracted.description}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        HorizontalDivider()

        // Star ratings
        Text(
            text = stringResource(R.string.review_rate_experience),
            style = MaterialTheme.typography.titleMedium,
        )

        InteractiveStarRating(
            rating = ratingQuality,
            onRatingChanged = { viewModel.setRatingQuality(it) },
            label = stringResource(R.string.rating_quality),
        )
        InteractiveStarRating(
            rating = ratingCommunication,
            onRatingChanged = { viewModel.setRatingCommunication(it) },
            label = stringResource(R.string.rating_communication),
        )
        InteractiveStarRating(
            rating = ratingTimeliness,
            onRatingChanged = { viewModel.setRatingTimeliness(it) },
            label = stringResource(R.string.rating_timeliness),
        )
        InteractiveStarRating(
            rating = ratingValue,
            onRatingChanged = { viewModel.setRatingValue(it) },
            label = stringResource(R.string.rating_value),
        )

        HorizontalDivider()

        // Title
        Text(
            text = stringResource(R.string.review_title_label),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = reviewTitle,
            onValueChange = { viewModel.setReviewTitle(it) },
            placeholder = { Text(stringResource(R.string.review_title_placeholder)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // Body
        Text(
            text = stringResource(R.string.review_body_label),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = reviewBody,
            onValueChange = { viewModel.setReviewBody(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 6,
        )

        // Verified badge
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Verified,
                contentDescription = null,
                tint = Green,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.review_invoice_verified),
                style = MaterialTheme.typography.bodySmall,
                color = Green,
            )
        }

        // Submit
        Button(
            onClick = { viewModel.submitReview() },
            enabled = viewModel.canSubmitReview && !isSubmitting,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = stringResource(R.string.review_submit),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}
