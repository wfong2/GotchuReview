package com.gotchureviews.app.ui.history

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gotchureviews.app.R
import com.gotchureviews.app.data.model.DetailedInvoice
import com.gotchureviews.app.ui.components.ReadOnlyField
import com.gotchureviews.app.ui.components.StarRating
import com.gotchureviews.app.ui.navigation.Screen
import com.gotchureviews.app.util.formatCurrency
import com.gotchureviews.app.util.formatInvoiceDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    invoice: DetailedInvoice,
    navController: NavController,
    imageStore: com.gotchureviews.app.data.local.InvoiceImageStore = hiltInvoiceImageStore(),
) {
    val bitmap: Bitmap? = remember(invoice.documentHash) {
        imageStore.loadBitmap(invoice.documentHash)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(stringResource(R.string.invoice_detail_title)) })

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Invoice image
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            navController.navigate(Screen.FullImage(invoice.documentHash))
                        },
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Photo,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.invoice_image_not_available),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // Extracted data
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ReadOnlyField(
                        label = stringResource(R.string.scan_total),
                        value = invoice.totalAmount.formatCurrency(),
                    )
                    invoice.laborCost?.let {
                        ReadOnlyField(label = stringResource(R.string.scan_labor), value = it.formatCurrency())
                    }
                    invoice.materialsCost?.let {
                        ReadOnlyField(label = stringResource(R.string.scan_materials), value = it.formatCurrency())
                    }
                    invoice.hourlyRate?.let {
                        ReadOnlyField(label = stringResource(R.string.invoice_hourly_rate), value = "${it.formatCurrency()}/hr")
                    }
                    invoice.projectDuration?.let {
                        ReadOnlyField(label = stringResource(R.string.invoice_duration), value = it)
                    }
                    invoice.invoiceDate?.let {
                        ReadOnlyField(label = stringResource(R.string.scan_date), value = it.formatInvoiceDate())
                    }
                    if (invoice.description.isNotEmpty()) {
                        ReadOnlyField(label = stringResource(R.string.scan_work), value = invoice.description)
                    }
                    ReadOnlyField(label = stringResource(R.string.invoice_currency), value = invoice.currency)
                }
            }

            // Line items
            if (invoice.lineItems.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.invoice_line_items),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(Modifier.height(8.dp))

                        invoice.lineItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = item.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        if (item.quantity > 1) {
                                            Text(
                                                text = "· qty ${item.quantity} @ ${item.effectiveUnitPrice.formatCurrency()}/ea",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = item.amount.formatCurrency(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }

            // Review
            invoice.review?.let { review ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Review",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StarRating(rating = review.overallRating)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = review.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
