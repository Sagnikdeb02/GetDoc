package com.example.getdoc.ui.patient.appointment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.R
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.ui.patient.component.sampleDoctor
import com.example.getdoc.ui.patient.component.CustomAppBar
import com.example.getdoc.ui.patient.component.DoctorCard

@Composable
fun ProceedScreen(doctor: DoctorInfo, onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            CustomAppBar(
                title = "Proceed",
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
                DoctorCard(
                    name = doctor.name,
                    specialty = doctor.specialization,
                    experience = doctor.experience,
                    fee = doctor.consultingFee,
                    doctorImage = doctor.profileImage,
                    rating = doctor.rating
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Patient Details Section
                Text(
                    text = "Patient Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Button(
                    onClick = { /* Navigate to Add Patient screen */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAF9F6))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_patient),
                            contentDescription = "Add Patient",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black // Use desired icon color
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Patient",
                            color = Color.Black, // Use desired text color
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Details Section
                Text(
                    text = "Payment Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(text = "Consultation Fee: ৳${doctor.consultingFee}")
                        Text(text = "Booking Charge: ৳2.50")
                        Text(text = "Hospital Charge: --")
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color.Gray, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total Amount: ৳${doctor.consultingFee + 2.50}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Terms and Conditions
                Text(
                    text = "Terms And Conditions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "The document governing the contractual relationship between the provider of a service and its user. On the web, this document is often also called 'Terms of Service' (ToS).",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Book Appointment Button
                Button(
                    onClick = { /* Handle book appointment logic */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Book Appointment")
                }
            }
        }
    )
}

// Sample doctor data and preview
@Preview(showSystemUi = true)
@Composable
fun ProceedScreenPreview() {
    ProceedScreen(
        doctor = sampleDoctor,
        onBackClick = { /* Handle Back Navigation */ },
    )
}
