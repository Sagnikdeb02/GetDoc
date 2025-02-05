package com.example.getdoc.ui.doctor.profile

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.getdoc.R
import com.example.getdoc.convertImageByteArrayToBitmap
import com.example.getdoc.fetchProfilePictureDynamically
import com.example.getdoc.fetchUsernameDynamically
import com.example.getdoc.ui.authentication.AuthenticationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client

enum class DoctorProfileOption(val displayName: String) {
    MY_CREDENTIALS("My Credentials"),
    DELETE_ACCOUNT("Delete Account"),
    CHANGE_PASSWORD("Change Password"),
    ABOUT_US("About Us"),
    HELP("Help")
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DoctorProfileScreen(
    navController: NavHostController,
    firestore: FirebaseFirestore,
    client: Client,
    onOptionClick: (DoctorProfileOption) -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit
) {
    val viewModel: AuthenticationViewModel = viewModel()
    val context = LocalContext.current
    var username by remember { mutableStateOf("Loading...") }
    var profileImage by remember { mutableStateOf<ByteArray?>(null) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        username = fetchUsernameDynamically(firestore, userId)
        profileImage = fetchProfilePictureDynamically(client, firestore, userId)
    }

    LaunchedEffect(Unit) {
        viewModel.init(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctor Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black) },
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

            // Profile Box with Glassmorphic Light Theme
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
                    // Profile Image with Soft Glow Border
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

                    // Username
                    Text(
                        text = username,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Edit Profile Button
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

            // Profile Options
            DoctorProfileOption.entries.forEach { option ->
                ProfileOptionItemComponent(
                    option = option.displayName,
                    onClick = { onOptionClick(option) }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Logout Button
            LogoutSectionComponent { onLogoutClick() }
        }
    }
}

/**
 * Profile Option Item Component - Clean UI
 */
@Composable
fun ProfileOptionItemComponent(
    option: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFFF1F5F9), Color(0xFFDDE5ED))))
            .clickable { onClick() }
            .padding(16.dp)
            .animateContentSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = option,
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Icon(
            painter = painterResource(id = R.drawable.img_11),
            contentDescription = "Arrow",
            tint = Color(0xFF60A5FA),
            modifier = Modifier.size(22.dp)
        )
    }
}

/**
 * Logout Section - Soft UI
 */
@Composable
fun LogoutSectionComponent(onLogoutClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFFE2E8F0), Color(0xFFC1D5E0))))
            .clickable { onLogoutClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Logout",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Logout",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}