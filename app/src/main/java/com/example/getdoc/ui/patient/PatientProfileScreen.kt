package com.example.getdoc.ui.patient


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.getdoc.R
import com.example.getdoc.navigation.PatientProfileScreen
import com.example.getdoc.ui.patient.state.PatientProfileUiState

@Composable
fun PatientProfileScreen(
    uiState: PatientProfileUiState,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
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



        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .size(80.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (uiState.imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(uiState.imageUri),
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

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = uiState.username,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // Edit Icon
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { },
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Options List
        val options = listOf(
            "My Appointments",
            "Medical History",
            "Change Contact",
            "Change Location",
            "Change Password",
            "About Us",
            "Help"
        )

        options.forEach { option ->
            ProfileOptionItem(option)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Handle Logout */ }
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
                contentDescription = "LogOut",
                tint = Color(0xFF1565C0)
            )
        }
    }
}

@Composable
fun ProfileOptionItem(option: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { },
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

@Preview
@Composable
private fun PatientProfileScreenPrev() {
    //
}