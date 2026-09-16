package com.gotchureviews.app.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gotchureviews.app.data.local.InvoiceImageStore
import com.gotchureviews.app.data.model.TradeCategory
import com.gotchureviews.app.data.model.VendorGroup
import com.gotchureviews.app.ui.components.InvoiceRow
import com.gotchureviews.app.ui.navigation.Screen
import com.gotchureviews.app.util.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorInvoiceListScreen(
    vendor: VendorGroup,
    vendorIndex: Int,
    navController: NavController,
    imageStore: InvoiceImageStore = hiltInvoiceImageStore(),
) {
    val displayName = vendor.contractor.businessName.ifEmpty { vendor.contractor.name }
    val categoryDisplay = TradeCategory.fromApiValue(vendor.contractor.category)?.name?.lowercase()?.replaceFirstChar { it.uppercase() }
        ?: vendor.contractor.category.replaceFirstChar { it.uppercase() }
    val location = listOfNotNull(vendor.contractor.city, vendor.contractor.state)
        .filter { it.isNotEmpty() }
        .joinToString(", ")

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(displayName) })

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Vendor header
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val category = TradeCategory.fromApiValue(vendor.contractor.category)
                            if (category != null) {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(
                                text = categoryDisplay,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        if (location.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = location,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        Text(
                            text = "${vendor.totalSpent.formatCurrency()} total across ${vendor.invoiceCount} ${if (vendor.invoiceCount == 1) "invoice" else "invoices"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Invoices
            itemsIndexed(vendor.invoices, key = { _, inv -> inv.id }) { index, invoice ->
                val thumbnail = remember(invoice.documentHash) {
                    imageStore.thumbnail(invoice.documentHash)
                }
                InvoiceRow(
                    invoice = invoice,
                    thumbnail = thumbnail,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.InvoiceDetail(vendorIndex, index))
                    },
                )
            }
        }
    }
}

@Composable
fun hiltInvoiceImageStore(): InvoiceImageStore {
    // This is a simple accessor; in production you might use Hilt's hiltViewModel or
    // LocalContext-based injection. For simplicity, we create from context.
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember { InvoiceImageStore(context) }
}
