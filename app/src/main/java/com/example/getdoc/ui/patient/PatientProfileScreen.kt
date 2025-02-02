package com.example.getdoc.ui.patient

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.R
import com.example.getdoc.convertImageByteArrayToBitmap
import com.example.getdoc.fetchProfilePictureDynamically
import com.example.getdoc.fetchUsernameDynamically
import com.example.getdoc.ui.doctor.profile.LogoutSectionComponent
import com.example.getdoc.ui.doctor.profile.ProfileOptionItemComponent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client

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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    firestore: FirebaseFirestore,
    client: Client,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit,
    onOptionClick: (PatientProfileOption) -> Unit,
    onEditClick: () -> Unit,
    patientViewModel: PatientViewModel
) {
    var username by remember { mutableStateOf("Loading...") }
    var profileImage by remember { mutableStateOf<ByteArray?>(null) }
    var userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    LaunchedEffect(userId) {
        username = fetchUsernameDynamically(firestore, userId)
        profileImage = fetchProfilePictureDynamically(client, firestore, userId)
    }

    Scaffold(

        bottomBar = {
            // Logout Section
            LogoutSectionComponent(
                onLogoutClick = onLogoutClick
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Profile Heading
            Text(
                text = "Profile",
                fontSize = 30.sp,  // Increased Font Size
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Profile Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 🔹 Profile Image
                Box(
                    modifier = Modifier
                        .size(100.dp)  // Increased Profile Picture Size
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    profileImage?.let {
                        Image(
                            bitmap = convertImageByteArrayToBitmap(it).asImageBitmap(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)  // Adjusted Size
                                .clip(CircleShape)
                        )
                    } ?: Icon(
                        painter = painterResource(id = R.drawable.profile),  // Default Profile Icon
                        contentDescription = "Default Profile",
                        modifier = Modifier.size(70.dp),  // Adjusted Default Icon Size
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 🔹 Username Display
                Text(
                    text = username,
                    fontSize = 24.sp,  // Increased Font Size
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

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


        Spacer(modifier = Modifier.width(10.dp))


        // Edit Icon
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit",
            modifier = Modifier
                .size(35.dp)
                .clickable { onEditClick() },
            tint = Color.Black
        )
    }
}

