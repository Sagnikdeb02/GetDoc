package com.example.getdoc.ui.patient

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Brush
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


enum class PatientProfileOption(val displayName: String) {
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
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        username = fetchUsernameDynamically(firestore, userId)
        profileImage = fetchProfilePictureDynamically(client, firestore, userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFAFAFA), Color.White, Color(0xFFF1F5F9))
                    )
                )
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Profile Box
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFFFFF))
                    .border(
                        3.dp,
                        Brush.linearGradient(
                            listOf(Color(0xFFC2E9FB), Color(0xFFA1C4FD))
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6))
                            .border(4.dp, Brush.linearGradient(listOf(Color(0xFF60A5FA), Color(0xFF8B5CF6))), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        profileImage?.let {
                            Image(
                                bitmap = convertImageByteArrayToBitmap(it).asImageBitmap(),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(CircleShape)
                            )
                        } ?: Icon(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Default Profile",
                            modifier = Modifier.size(90.dp),
                            tint = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = username,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onEditClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF60A5FA)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Edit Profile", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            PatientProfileOption.entries.forEach { option ->
                ProfileOptionItemComponent(
                    option = option.displayName,
                    onClick = { onOptionClick(option) }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            LogoutSectionComponent { onLogoutClick() }
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

