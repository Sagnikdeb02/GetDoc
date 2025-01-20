package com.example.getdoc.ui.authentication.doctor_registration

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.getdoc.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import coil.compose.rememberImagePainter
import com.example.getdoc.ui.authentication.AuthenticationViewModel

//
//@Composable
//fun UploadLicenseScreen(
//    modifier: Modifier = Modifier,
//    viewModel: AuthenticationViewModel = AuthenticationViewModel(),
//) {
//    val context = LocalContext.current
//    val uploadedImageUri = remember { mutableStateOf<Uri?>(null) }
//    val showConfirmationMessage = remember { mutableStateOf(false) }
//
//    // Image picker launcher
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        if (uri != null) {
//            uploadedImageUri.value = uri
//            viewModel.uploadLicense(uri)
//            showConfirmationMessage.value = true // Show the confirmation message after upload
//        } else {
//            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Button(
//            onClick = {
//                launcher.launch("image/*")
//            },
//            modifier = Modifier
//                .padding(bottom = 16.dp)
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_upload), // Replace with your upload icon resource
//                contentDescription = "Upload Icon",
//                modifier = Modifier.size(24.dp)
//            )
//            Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
//            Text("Upload License")
//        }
//
//        Text(
//            text = "You must upload your license certificate picture.",
//            style = MaterialTheme.typography.bodyMedium,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.padding(top = 16.dp)
//        )
//
//        // Display the uploaded image in a small size if available
//        uploadedImageUri.value?.let { uri ->
//            Image(
//                painter = rememberImagePainter(uri),
//                contentDescription = "Uploaded License Image",
//                modifier = Modifier
//                    .size(100.dp)
//                    .padding(top = 16.dp)
//            )
//        }
//
//        // Animate and show "waiting for admin confirmation" message
//        if (showConfirmationMessage.value) {
//            AnimatedVisibility(
//                visible = showConfirmationMessage.value,
//                enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
//                exit = fadeOut(animationSpec = tween(durationMillis = 1000))
//            ) {
//                Text(
//                    text = "Waiting for admin confirmation...",
//                    style = MaterialTheme.typography.bodyMedium,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.padding(top = 16.dp)
//                )
//                AdminApprovalScreen( viewModel = AdminViewModel())
//            }
//        }
//    }
//}
//
//
//@Composable
//fun LicenseConfirmationScreen(
//    modifier: Modifier = Modifier
//) {
//
//
//    Box(
//        modifier = modifier
//            .fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            "License Uploaded. Wait for the confirmation. Thank you!"
//        )
//    }
//
//}
//
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewLicenseConfirmationScreen() {
//    LicenseConfirmationScreen()
//}
