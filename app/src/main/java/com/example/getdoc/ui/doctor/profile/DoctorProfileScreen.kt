package com.example.getdoc.ui.doctor.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.getdoc.R
import com.example.getdoc.navigation.LoginScreen
import com.example.getdoc.ui.authentication.AuthenticationViewModel
import kotlinx.coroutines.launch
@Composable
fun DoctorProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit,
    onOptionClick: (String) -> Unit,
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val authViewModel: AuthenticationViewModel = viewModel()
    val context = LocalContext.current  // Move outside LaunchedEffect

    LaunchedEffect(Unit) {
        authViewModel.init(context)  // Use context safely here
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F7))
    ) {
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

        // Profile Information Row
        ProfileInfoRowComponent(
            modifier = Modifier.padding(16.dp),
            onEditClick = { /* Handle Edit */ }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Options List
        val options = listOf(
            "My Appointments",
            "My Credentials",
            "Change Contact",
            "Change Location",
            "Change Password",
            "About Us",
            "Help"
        )

        options.forEach { option ->
            ProfileOptionItemComponent(
                option = option,
                onClick = { onOptionClick(option) }
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 100.dp))

        // Logout Section
        LogoutSectionComponent(
            modifier = Modifier.padding(16.dp),
            viewModel = authViewModel,
            navController = navController
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        BottomBarComponent(
            onHomeClick = onHomeClick,
            onAppointmentsClick = onAppointmentsClick,
            onProfileClick = onProfileClick
        )
    }
}

/**
 * Profile Info Row component
 */
@Composable
fun ProfileInfoRowComponent(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(80.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.img_7),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Christopher",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "01787985785",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Sylhet",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }

        // Edit Icon
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit",
            modifier = Modifier.size(24.dp),
            tint = Color.Black,
            //onClick = onEditClick
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
    viewModel: AuthenticationViewModel,
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                coroutineScope.launch {
                    viewModel.logout()
                    navController.navigate(LoginScreen) {
                        popUpTo(LoginScreen) { inclusive = true }
                    }
                }
            }
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
        Icon(
            imageVector = Icons.Default.ExitToApp,
            contentDescription = "Logout",
            tint = Color(0xFF1565C0)
        )
    }
}
