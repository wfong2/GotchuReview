package com.gotchureviews.app.ui.main

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gotchureviews.app.R
import com.gotchureviews.app.ui.explore.ExploreTab
import com.gotchureviews.app.ui.history.HistoryTab
import com.gotchureviews.app.ui.onboarding.OnboardingScreen
import com.gotchureviews.app.ui.onboarding.OnboardingViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onSignOut: () -> Unit,
    onboardingViewModel: OnboardingViewModel = hiltViewModel(),
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val isSignedIn by onboardingViewModel.isSignedIn.collectAsState()
    var showSignIn by remember { mutableStateOf(false) }

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
            0 -> ExploreTab(
                modifier = Modifier.padding(padding),
                onSignOut = onSignOut,
            )
            1 -> {
                if (isSignedIn) {
                    ScanScreen(modifier = Modifier.padding(padding))
                } else {
                    SignInPromptScreen(
                        icon = Icons.Filled.CameraAlt,
                        message = stringResource(R.string.sign_in_to_scan),
                        onSignIn = { showSignIn = true },
                        modifier = Modifier.padding(padding),
                    )
                }
            }
            2 -> {
                if (isSignedIn) {
                    HistoryTab(
                        modifier = Modifier.padding(padding),
                        onSignOut = onSignOut,
                    )
                } else {
                    SignInPromptScreen(
                        icon = Icons.Filled.History,
                        message = stringResource(R.string.sign_in_to_history),
                        onSignIn = { showSignIn = true },
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }

    if (showSignIn) {
        ModalBottomSheet(
            onDismissRequest = { showSignIn = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onOnboardingComplete = { showSignIn = false },
                showGuestOption = false,
            )
        }
    }
}

@Composable
private fun SignInPromptScreen(
    icon: ImageVector,
    message: String,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onSignIn,
            shape = RoundedCornerShape(14.dp),
        ) {
            Icon(Icons.Filled.Person, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.common_sign_in))
        }
    }
}
