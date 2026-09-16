package com.gotchureviews.app.ui.scan

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gotchureviews.app.R
import com.gotchureviews.app.ui.components.ContractorMatchCard
import com.gotchureviews.app.ui.components.ReadOnlyField
import com.gotchureviews.app.ui.theme.Green
import com.gotchureviews.app.ui.theme.Orange
import com.gotchureviews.app.util.formatCurrency

@Composable
fun InvoiceExtractedScreen(
    viewModel: ScanViewModel,
    modifier: Modifier = Modifier,
) {
    val extractionResult by viewModel.extractionResult.collectAsState()
    val selectedContractorId by viewModel.selectedContractorId.collectAsState()
    val isNewContractor by viewModel.isNewContractor.collectAsState()
    var showDuplicateAlert by remember { mutableStateOf(false) }

    val result = extractionResult ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Green,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.scan_extracted),
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        // Contractor name
        Text(
            text = stringResource(R.string.scan_contractor),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = result.extracted.contractorName,
            style = MaterialTheme.typography.titleMedium,
        )

        // Contractor match
        if (result.contractorMatches.isNotEmpty()) {
            ContractorMatchCard(
                matches = result.contractorMatches,
                selectedId = selectedContractorId,
                isNewContractor = isNewContractor,
                onSelectExisting = { viewModel.setSelectedContractorId(it) },
                onSelectNew = { viewModel.setIsNewContractor(true) },
            )
        } else {
            Text(
                text = stringResource(R.string.scan_new_contractor),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        HorizontalDivider()

        // Extracted fields
        ReadOnlyField(
            label = stringResource(R.string.scan_total),
            value = result.extracted.totalAmount.formatCurrency(),
        )

        result.extracted.laborCost?.let {
            ReadOnlyField(
                label = stringResource(R.string.scan_labor),
                value = it.formatCurrency(),
            )
        }

        result.extracted.materialsCost?.let {
            ReadOnlyField(
                label = stringResource(R.string.scan_materials),
                value = it.formatCurrency(),
            )
        }

        result.extracted.invoiceDate?.let {
            ReadOnlyField(
                label = stringResource(R.string.scan_date),
                value = it,
            )
        }

        ReadOnlyField(
            label = stringResource(R.string.scan_work),
            value = result.extracted.description,
        )

        if (result.estimatedBreakdown) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = Orange,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.scan_estimated_breakdown),
                    style = MaterialTheme.typography.labelSmall,
                    color = Orange,
                )
            }
        }

        HorizontalDivider()

        // Rescan
        TextButton(onClick = { viewModel.rescan() }) {
            Text(stringResource(R.string.scan_rescan))
        }

        // Confirm
        Button(
            onClick = {
                if (selectedContractorId != null) {
                    showDuplicateAlert = true
                } else {
                    viewModel.confirmExtraction()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text(
                text = stringResource(R.string.scan_confirm),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }

    if (showDuplicateAlert) {
        AlertDialog(
            onDismissRequest = { showDuplicateAlert = false },
            title = { Text(stringResource(R.string.scan_duplicate_check)) },
            text = { Text(stringResource(R.string.scan_duplicate_check_message)) },
            dismissButton = {
                TextButton(onClick = {
                    showDuplicateAlert = false
                    viewModel.rescan()
                }) {
                    Text(stringResource(R.string.scan_discard))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDuplicateAlert = false
                    viewModel.confirmExtraction()
                }) {
                    Text(stringResource(R.string.scan_new_invoice))
                }
            },
        )
    }
}
