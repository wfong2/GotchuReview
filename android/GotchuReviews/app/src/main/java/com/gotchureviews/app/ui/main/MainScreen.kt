package com.gotchureviews.app.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.gotchureviews.app.R
import com.gotchureviews.app.ui.explore.ExploreTab
import com.gotchureviews.app.ui.history.HistoryTab
import com.gotchureviews.app.ui.scan.ScanScreen

private data class TabItem(
    val labelRes: Int,
    val icon: ImageVector,
)

private val tabs = listOf(
    TabItem(R.string.tab_explore, Icons.Filled.Search),
    TabItem(R.string.tab_scan, Icons.Filled.CameraAlt),
    TabItem(R.string.tab_history, Icons.Filled.History),
)

@Composable
fun MainScreen(
    onSignOut: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.labelRes)) },
                    )
                }
            }
        },
    ) { padding ->
        when (selectedTab) {
            0 -> ExploreTab(modifier = Modifier.padding(padding))
            1 -> ScanScreen(modifier = Modifier.padding(padding))
            2 -> HistoryTab(
                modifier = Modifier.padding(padding),
                onSignOut = onSignOut,
            )
        }
    }
}
