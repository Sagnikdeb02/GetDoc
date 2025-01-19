package com.example.getdoc.ui.patient

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.getdoc.R
import com.example.getdoc.ui.patient.state.PatientProfileUiState

@Composable
fun PatientProfileInputScreen(
    uiState: PatientProfileUiState,
    onUsernameChange: (TextFieldValue) -> Unit,
    onImageUriChange: (Uri?) -> Unit,
    onUpdateClick: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onImageUriChange(uri)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                if (uiState.imageUri == null) {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        shape = CircleShape,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text("Upload Image", textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(uiState.imageUri),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = uiState.usernameInput,
                onValueChange = onUsernameChange,
                label = { Text("Enter Username") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onUpdateClick,
                modifier = Modifier.fillMaxWidth(.3f)
            ) {
                Text("Update")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PatientProfileInputScreenPreview() {
    // Mock UI state
    val mockUiState = PatientProfileUiState(
        usernameInput = TextFieldValue(""),
        imageUri = null // Use a valid Uri if you want to preview an image
    )

    // Call the composable with mock data
    PatientProfileInputScreen(
        uiState = mockUiState,
        onUsernameChange = {}, // No-op for preview
        onImageUriChange = {}, // No-op for preview
        onUpdateClick = {}     // No-op for preview
    )
}