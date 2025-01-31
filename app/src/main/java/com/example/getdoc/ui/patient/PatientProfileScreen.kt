package com.example.getdoc.ui.patient


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.getdoc.R
import com.example.getdoc.navigation.PatientProfileScreen
import com.example.getdoc.ui.authentication.AuthenticationViewModel
import com.example.getdoc.ui.doctor.profile.DoctorProfileOption
import com.example.getdoc.ui.doctor.profile.LogoutSectionComponent
import com.example.getdoc.ui.doctor.profile.ProfileOptionItemComponent
import com.example.getdoc.ui.patient.state.PatientProfileUiState
import com.google.firebase.auth.FirebaseAuth

enum class PatientProfileOption(
    val displayName: String
) {
    MY_APPOINTMENTS("My Appointments"),
    MEDICAL_HISTORY("Medical History"),
    CHANGE_CONTACT("Change Contact"),
    CHANGE_PASSWORD("Change Password"),
    ABOUT_US("About Us"),
    HELP("Help")
}
@Composable
fun PatientProfileScreen(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit,
    onOptionClick: (PatientProfileOption) -> Unit,
    onEditClick: () -> Unit,
    patientViewModel: PatientViewModel
) {
    // TODO: Remove this after implementing authentication
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    LaunchedEffect(userId) {
       if(userId.isNullOrEmpty()){
           patientViewModel.fetchPatientProfile()
       }
    }


    Scaffold(
        modifier = modifier,
        topBar = {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFF1565C0))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(10.dp)
                )
                Image(painter = painterResource(id = R.drawable.img_9), contentDescription = "")
            }
        },
        bottomBar = {
            // Logout Section
            LogoutSectionComponent(
                onLogoutClick = onLogoutClick
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            PatientProfileInfoRowComponent(
                userId = userId,
                viewModel = patientViewModel,
                onEditClick = { onEditClick() }
            )

            Spacer(modifier = Modifier.height(40.dp))


            PatientProfileOption.entries.forEach { option ->
                ProfileOptionItemComponent(
                    option = option.displayName,
                    onClick = { onOptionClick(option) }
                )
            }
        }
    }

}

@Composable
fun PatientProfileInfoRowComponent(
    modifier: Modifier = Modifier,
    userId: String, // Pass userId to fetch profile data
    viewModel: PatientViewModel, // Pass ViewModel to manage state
    onEditClick: () -> Unit
) {
    // Collect UI state from ViewModel
    val profileUiState by viewModel.profileUiState.collectAsState()

    // Call fetchPatientProfile when the composable is first launched
    LaunchedEffect(userId) {
        if (profileUiState.usernameInput.isEmpty() || profileUiState.profileImageBitmap == null) {
            viewModel.fetchPatientProfile()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (profileUiState.isLoading) {
                // Show loading indicator while fetching data
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 2.dp
                    )
                }
            } else {
                // Display profile picture if available
                if (profileUiState.profileImageBitmap != null) {
                    Image(
                        bitmap = profileUiState.profileImageBitmap!!.asImageBitmap(),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_7),
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = if (profileUiState.errorMessage == null) {
                        profileUiState.usernameInput.ifEmpty { "Unknown" }
                    } else {
                        "Failed to load profile"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = if (profileUiState.errorMessage == null) {
                        profileUiState.locationInput.ifEmpty { "Unknown" }
                    } else {
                        "Try again later"
                    },
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }

        // Edit Icon
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit",
            modifier = Modifier
                .size(24.dp)
                .clickable { onEditClick() },
            tint = Color.Black
        )
    }
}
