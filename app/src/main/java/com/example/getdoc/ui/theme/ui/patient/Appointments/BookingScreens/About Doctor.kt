package com.example.getdoc.ui.theme.ui.patient.Appointments.BookingScreens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.ui.theme.ui.patient.Appointments.BookingScreenElements.DoctorInfo
import com.example.getdoc.ui.theme.ui.patient.Appointments.BookingScreenElements.sampleDoctor
import com.example.getdoc.ui.theme.ui.patient.Appointments.BookingScreenElements.CustomAppBar
import com.example.getdoc.ui.theme.ui.patient.Appointments.BookingScreenElements.ReviewItem

@Composable
fun DoctorDetailsScreen(doctor: DoctorInfo, onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            CustomAppBar(
                title = doctor.name,
                onBackClick = onBackClick
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Profile Section
                Row( verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth() ){
                    Image(
                        painter = painterResource(id = doctor.profileImage),
                        contentDescription = "Doctor Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.Gray, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = doctor.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(text = "${doctor.degree}, ${doctor.specialization}")
                        Text(text = "${doctor.experience} Years Experience")
                       Row{ Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(text = doctor.location)}
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Rating and Fee Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Consulting Fee: ৳${doctor.consultingFee}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "⭐ ${doctor.rating}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                )  {
                    Button(
                        onClick = { /* TODO: Handle Book Now */ },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text(text = "Book Now")
                    }
                    Button(
                        onClick = { /* TODO: Handle Call */ },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text(text = "Call")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // About Doctor Section
                Text(
                    text = "About Doctor",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = doctor.about,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Reviews Section
                Text(
                    text = "Reviews (23)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                repeat(2) {
                    ReviewItem(
                        name = "Sam",
                        date = "12/12/2023",
                        review = doctor.about
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                }}
        }
    )

}

@Preview(showSystemUi = true)
@Composable
fun DoctorDetailsScreenPreview() {
    DoctorDetailsScreen(
        doctor = sampleDoctor,
        onBackClick = { /* Handle Back Navigation */ }
    )
}
