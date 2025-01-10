package com.example.getdoc.ui.authentication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.getdoc.navigation.LicenseConfirmationScreen

@Composable
fun UploadLicenseScreen(
    modifier: Modifier = Modifier,
    onImageUpload: () -> Unit,
) {

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onImageUpload,
        ) {
            Text("Upload License")
        }
    }
}

@Composable
fun LicenseConfirmationScreen(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "License Uploaded. Wait for the confirmation. Thank you!"
        )
    }

}