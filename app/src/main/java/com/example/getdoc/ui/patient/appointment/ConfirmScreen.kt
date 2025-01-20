package com.example.getdoc.ui.patient.appointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.ui.patient.component.DoctorCard
import com.example.getdoc.ui.patient.component.sampleDoctor

@Composable
fun ConfirmScreen(
    bookingId: String,
    doctor: DoctorInfo, // Pass DoctorInfoTwo object here
    onBackToHomeClick: () -> Unit // Add a callback for the "Back to Home" button
) {
    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Confirmation Message
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Thank You",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your Booking is Successfully Completed",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Booking ID: $bookingId",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Doctor's Information
                DoctorCard(
                    name = doctor.name,
                    specialty = doctor.specialization,
                    experience = doctor.experience,
                    fee = doctor.consultingFee,
                    doctorImage = doctor.profileImage,
                    rating = doctor.rating
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Back to Home Button
                Button(
                    onClick = onBackToHomeClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Back to Home Page",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun ConfirmScreenPreview() {
    ConfirmScreen(
        bookingId = 1234567890.toString(),
        doctor = sampleDoctor,
        onBackToHomeClick = { /* Handle Back to Home */ }
    )
}
