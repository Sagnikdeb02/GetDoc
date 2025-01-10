package com.example.getdoc.ui.patient.appointment



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.ui.patient.component.CustomButton
import com.example.getdoc.ui.patient.component.CustomAppBar

@Composable
fun Proceed2ndScreen(
    patientName: String,
    patientGender: String,
    patientAge: String,
    bookingFor: String,
    consultationFee: Double,
    bookingCharge: Double,
    hospitalCharge: Double,
    onBookAppointmentClick: () -> Unit,
    onBackClick: () -> Unit
) {
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
                // Patient Details
                Text(
                    text = "Patient Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Name: $patientName", fontSize = 16.sp)
                        Text(text = "Gender: $patientGender", fontSize = 16.sp)
                        Text(text = "Age: $patientAge", fontSize = 16.sp)
                        Text(text = "Booking For: $bookingFor", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Details
                Text(
                    text = "Payment Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Consultation Fee: ৳$consultationFee", fontSize = 16.sp)
                        Text(text = "Booking Charge: ৳$bookingCharge", fontSize = 16.sp)
                        Text(text = "Hospital Charge: ৳$hospitalCharge", fontSize = 16.sp)
                        Divider(
                            color = Color.Gray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = "Total Amount: ৳${consultationFee + bookingCharge + hospitalCharge}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Terms and Conditions
                Text(
                    text = "Terms and Conditions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "By proceeding, you agree to the terms and conditions.",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Book Appointment Button
                CustomButton(
                    buttonText = "Book Appointment",
                    onClick = { /* Handle book appointment logic */ }
                )
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun Proceed2ndScreenPreview() {
    Proceed2ndScreen(
        patientName = "Sagnik Dey",
        patientGender = "Male",
        patientAge = "32 Years Old",
        bookingFor = "Self",
        consultationFee = 500.0,
        bookingCharge = 2.50,
        hospitalCharge = 100.0,
        onBookAppointmentClick = { /* Handle Booking */ },
        onBackClick = { /* Handle Back */ }
    )
}
