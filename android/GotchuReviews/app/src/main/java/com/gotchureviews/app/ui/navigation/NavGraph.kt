package com.gotchureviews.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gotchureviews.app.ui.main.MainScreen
import com.gotchureviews.app.ui.onboarding.OnboardingScreen
import com.gotchureviews.app.ui.onboarding.OnboardingViewModel

@Composable
fun GotchuNavGraph() {
    val navController = rememberNavController()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val hasCompletedOnboarding by onboardingViewModel.hasCompletedOnboarding.collectAsState()
    val isSignedIn by onboardingViewModel.isSignedIn.collectAsState()

    val startDestination: Screen = if (hasCompletedOnboarding && isSignedIn) {
        Screen.Main
    } else {
        Screen.Onboarding
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Screen.Onboarding> {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onOnboardingComplete = {
                    navController.navigate(Screen.Main) {
                        popUpTo(Screen.Onboarding) { inclusive = true }
                    }
                },
            )
        }

        composable<Screen.Main> {
            MainScreen(
                onSignOut = {
                    navController.navigate(Screen.Onboarding) {
                        popUpTo(Screen.Main) { inclusive = true }
                    }
                },
            )
        }
    }
}
