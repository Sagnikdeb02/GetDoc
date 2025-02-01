package com.example.getdoc.ui.doctor.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.getdoc.R
import com.example.getdoc.convertImageByteArrayToBitmap
import com.example.getdoc.fetchProfilePictureDynamically
import com.example.getdoc.fetchUsernameDynamically
import com.example.getdoc.navigation.LoginScreen
import com.example.getdoc.ui.authentication.AuthenticationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import kotlinx.coroutines.launch


enum class DoctorProfileOption(
    val displayName: String
) {
    MY_CREDENTIALS("My Credentials"),
    CHANGE_CONTACT("Change Contact"),
    CHANGE_PASSWORD("Change Password"),
    ABOUT_US("About Us"),
    HELP("Help")
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DoctorProfileScreen(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit,
    onOptionClick: (DoctorProfileOption) -> Unit,
    onEditClick: () -> Unit,
    firestore:FirebaseFirestore,
    client: Client
) {
    // TODO: Remove this after implementing authentication
    val viewModel: AuthenticationViewModel = viewModel()
    val context = LocalContext.current
    var username by remember { mutableStateOf("Loading...") }
    var profileImage by remember { mutableStateOf<ByteArray?>(null) }
    var userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    LaunchedEffect(userId) {
        username = fetchUsernameDynamically(firestore, userId)
        profileImage = fetchProfilePictureDynamically(client, firestore, userId)
    }
    LaunchedEffect(Unit) {
        viewModel.init(context)
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
            DoctorProfileInfoRowComponent(
                modifier = Modifier.padding(16.dp),
                onEditClick = { onEditClick() }
            )

            Spacer(modifier = Modifier.height(40.dp))


            DoctorProfileOption.entries.forEach { option ->
                ProfileOptionItemComponent(
                    option = option.displayName,
                    onClick = { onOptionClick(option) }
                )
            }
        }
    }

}

/**
 * Profile Info Row component
 */
@Composable
fun DoctorProfileInfoRowComponent(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit
) {
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

/**
 * Profile Option Item component
 */
@Composable
fun ProfileOptionItemComponent(
    option: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = option,
            fontSize = 16.sp,
            color = Color.Black
        )
        Icon(
            painter = painterResource(id = R.drawable.img_11), // Replace with your arrow resource
            contentDescription = "Arrow",
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Logout Section component
 */
@Composable
fun LogoutSectionComponent(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit,

) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Logout",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1565C0),
            modifier = Modifier.padding(end = 8.dp)
        )
        IconButton(
            onClick = onLogoutClick
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Logout",
                tint = Color(0xFF1565C0)
            )
        }
    }
}
