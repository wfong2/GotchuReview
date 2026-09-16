package com.gotchureviews.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gotchureviews.app.R
import com.gotchureviews.app.data.model.ContractorMatch
import com.gotchureviews.app.ui.theme.Green

@Composable
fun ContractorMatchCard(
    matches: List<ContractorMatch>,
    selectedId: String?,
    isNewContractor: Boolean,
    onSelectExisting: (String) -> Unit,
    onSelectNew: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val topMatch = matches.firstOrNull() ?: return

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.scan_match_found),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Green,
            )

            Text(
                text = topMatch.contractor.businessName.ifEmpty { topMatch.contractor.name },
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = "${topMatch.contractor.locationDisplay} · ${topMatch.contractor.reviewCount} ${stringResource(R.string.review_plural)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val isSelected = selectedId == topMatch.contractor.id
                if (isSelected) {
                    Button(onClick = { onSelectExisting(topMatch.contractor.id) }) {
                        Text(stringResource(R.string.scan_yes_same))
                    }
                } else {
                    OutlinedButton(onClick = { onSelectExisting(topMatch.contractor.id) }) {
                        Text(stringResource(R.string.scan_yes_same))
                    }
                }

                if (isNewContractor) {
                    Button(onClick = onSelectNew) {
                        Text(stringResource(R.string.scan_no_new))
                    }
                } else {
                    OutlinedButton(onClick = onSelectNew) {
                        Text(stringResource(R.string.scan_no_new))
                    }
                }
            }
        }
    }
}
