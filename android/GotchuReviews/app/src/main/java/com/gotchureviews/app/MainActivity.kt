package com.gotchureviews.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gotchureviews.app.ui.navigation.GotchuNavGraph
import com.gotchureviews.app.ui.theme.GotchuReviewsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GotchuReviewsTheme {
                GotchuNavGraph()
            }
        }
    }
}
