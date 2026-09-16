package com.gotchureviews.app.ui.scan

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.gotchureviews.app.R

@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    viewModel: ScanViewModel = hiltViewModel(),
) {
    val flowStep by viewModel.flowStep.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    when (flowStep) {
        ScanFlowStep.CAMERA -> CameraScreen(viewModel = viewModel, modifier = modifier)
        ScanFlowStep.EXTRACTING -> ExtractingScreen(modifier = modifier)
        ScanFlowStep.EXTRACTED -> InvoiceExtractedScreen(viewModel = viewModel, modifier = modifier)
        ScanFlowStep.REVIEW -> WriteReviewScreen(viewModel = viewModel, modifier = modifier)
        ScanFlowStep.SUBMITTED -> SubmittedScreen(viewModel = viewModel, modifier = modifier)
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text(stringResource(R.string.common_error)) },
            text = { Text(errorMessage ?: "") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text(stringResource(R.string.common_ok))
                }
            },
        )
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
