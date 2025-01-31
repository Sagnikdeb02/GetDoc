package com.example.getdoc.ui.patient.appointment

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.navigation.PatientHomeScreen
import com.example.getdoc.ui.patient.component.CustomAppBar
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun ConfirmScreen(
    bookingId: String,
    navController: NavController,
    firestore: FirebaseFirestore,
    onBackToHomeClick: () -> Unit
) {
    var doctor by remember { mutableStateOf<DoctorInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch Booking Details from Firestore
    LaunchedEffect(bookingId) {
        firestore.collection("appointments").document(bookingId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val doctorId = document.getString("doctorId")
                    if (doctorId != null) {
                        fetchDoctorInfo(firestore, doctorId) { fetchedDoctor ->
                            doctor = fetchedDoctor
                            isLoading = false
                        }
                    } else {
                        Log.e("Firestore", "Doctor ID not found in booking!")
                        isLoading = false
                    }
                } else {
                    Log.e("Firestore", "Booking not found!")
                    isLoading = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching booking details: ${e.message}")
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            CustomAppBar(title = "Booking Confirmation", onBackClick = { onBackToHomeClick() })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Thank You!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your booking is successfully completed.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Booking ID: $bookingId",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Show Doctor's Information
                    doctor?.let {
                        DoctorConfirmCard(it)
                    } ?: Text("Doctor details not available", color = Color.Red)

                    Spacer(modifier = Modifier.height(32.dp))

                    // ✅ Fixed Back to Home Button
                    Button(
                        onClick = {
                            navController.navigate(PatientHomeScreen) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "Back to Home",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    )
}

// Fetch doctor details from Firestore
fun fetchDoctorInfo(firestore: FirebaseFirestore, doctorId: String, onResult: (DoctorInfo?) -> Unit) {
    firestore.collection("doctors").document(doctorId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val doctor = document.toObject(DoctorInfo::class.java)
                onResult(doctor)
            } else {
                Log.e("Firestore", "Doctor not found!")
                onResult(null)
            }
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error fetching doctor details: ${e.message}")
            onResult(null)
        }
}

// Display Doctor Information
@Composable
fun DoctorConfirmCard(doctor: DoctorInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Doctor Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Name: Dr. ${doctor.name}")
            Text("Specialization: ${doctor.specialization}")
            Text("Experience: ${doctor.experience} years")
            Text("Consultation Fee: ৳${doctor.consultingFee}")
        }
    }
}
