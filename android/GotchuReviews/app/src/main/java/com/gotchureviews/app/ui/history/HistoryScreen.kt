package com.gotchureviews.app.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.gotchureviews.app.R
import com.gotchureviews.app.ui.components.SummaryCard
import com.gotchureviews.app.ui.components.VendorRow
import com.gotchureviews.app.ui.navigation.Screen
import com.gotchureviews.app.ui.settings.SettingsSheet
import com.gotchureviews.app.util.formatCurrency

@Composable
fun HistoryTab(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit,
) {
    val navController = rememberNavController()
    val viewModel: HistoryViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.History,
        modifier = modifier,
    ) {
        composable<Screen.History> {
            HistoryScreen(
                navController = navController,
                viewModel = viewModel,
                onSignOut = onSignOut,
            )
        }
        composable<Screen.VendorInvoiceList> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.VendorInvoiceList>()
            val vendors = viewModel.sortedVendors
            if (route.vendorIndex in vendors.indices) {
                VendorInvoiceListScreen(
                    vendor = vendors[route.vendorIndex],
                    vendorIndex = route.vendorIndex,
                    navController = navController,
                )
            }
        }
        composable<Screen.InvoiceDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.InvoiceDetail>()
            val vendors = viewModel.sortedVendors
            if (route.vendorIndex in vendors.indices) {
                val invoices = vendors[route.vendorIndex].invoices
                if (route.invoiceIndex in invoices.indices) {
                    InvoiceDetailScreen(
                        invoice = invoices[route.invoiceIndex],
                        navController = navController,
                    )
                }
            }
        }
        composable<Screen.FullImage> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.FullImage>()
            FullImageScreen(
                documentHash = route.documentHash,
                onClose = { navController.popBackStack() },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel,
    onSignOut: () -> Unit,
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val creditBalance by viewModel.creditBalance.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.history_title)) },
            actions = {
                Box {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Filled.SwapVert, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.history_sort_recent)) },
                            onClick = {
                                viewModel.setSortOption(VendorSortOption.RECENT_FIRST)
                                showSortMenu = false
                            },
                            trailingIcon = {
                                if (sortOption == VendorSortOption.RECENT_FIRST) {
                                    Text("✓")
                                }
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.history_sort_name)) },
                            onClick = {
                                viewModel.setSortOption(VendorSortOption.NAME)
                                showSortMenu = false
                            },
                            trailingIcon = {
                                if (sortOption == VendorSortOption.NAME) {
                                    Text("✓")
                                }
                            },
                        )
                    }
                }
                IconButton(onClick = { showSettings = true }) {
                    Icon(Icons.Filled.Settings, contentDescription = null)
                }
            },
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            val sortedVendors = viewModel.sortedVendors

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Summary cards
                summary?.let { s ->
                    if (s.totalInvoices > 0) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                SummaryCard(
                                    title = stringResource(R.string.history_total_spent),
                                    value = s.totalSpent.formatCurrency(),
                                    modifier = Modifier.weight(1f),
                                )
                                SummaryCard(
                                    title = stringResource(R.string.history_contractors),
                                    value = "${s.contractorCount}",
                                    modifier = Modifier.weight(1f),
                                )
                                SummaryCard(
                                    title = stringResource(R.string.history_jobs),
                                    value = "${s.totalInvoices}",
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }

                // Vendor list
                itemsIndexed(sortedVendors, key = { _, v -> v.id }) { index, vendor ->
                    VendorRow(
                        vendor = vendor,
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.VendorInvoiceList(index))
                        },
                    )
                }

                // Empty state
                if (sortedVendors.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Description,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.history_empty),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // Credits
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.history_credits),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = "$creditBalance",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSettings) {
        SettingsSheet(
            onDismiss = { showSettings = false },
            onSignOut = {
                showSettings = false
                onSignOut()
            },
        )
    }
}
