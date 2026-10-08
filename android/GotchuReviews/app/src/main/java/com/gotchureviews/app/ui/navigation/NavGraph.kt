package com.gotchureviews.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gotchureviews.app.ui.main.MainScreen

@Composable
fun GotchuNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Main,
    ) {
        composable<Screen.Main> {
            MainScreen(onSignOut = { /* stay on main, sign-in prompt handles it */ })
        }
    }
}
