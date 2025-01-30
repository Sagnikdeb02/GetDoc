package com.example.getdoc.ui.patient.appointment

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.ui.patient.PatientViewModel
import com.example.getdoc.ui.patient.component.CustomAppBar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun DoctorDetailsScreen(
    navController: NavController,
    doctorId: String,
    onBackClick: () -> Unit,
    viewModel: PatientViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var doctor by remember { mutableStateOf<DoctorInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch doctor details from Firestore
    LaunchedEffect(doctorId) {
        coroutineScope.launch {
            fetchDoctorById(doctorId) { fetchedDoctor ->
                doctor = fetchedDoctor
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                title = doctor?.name ?: "Doctor Details",
                onBackClick = onBackClick
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    doctor?.let {
                        // Doctor Information
                        Text(text = "Dr. ${it.name}", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Specialization: ${it.specialization}")
                        Text(text = "Degree: ${it.degree}")
                        Text(text = "Experience: ${it.experience} years")
                        Text(text = "Location: ${it.location}")
                        Text(text = "Consulting Fee: ${it.consultingFee} BDT")
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "About: ${it.about}")
                        Spacer(modifier = Modifier.height(16.dp))

                        // Approval Status
                        Text(
                            text = if (it.isApproved) "✅ Approved Doctor" else "⏳ Pending Approval",
                            color = if (it.isApproved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Add buttons for booking or calling
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {Button(onClick = {
                            if (doctorId.isNotEmpty()) {
                                Log.d("Navigation", "Navigating to booking_screen/$doctorId")
                                navController.navigate("booking_screen/$doctorId")
                            } else {
                                Log.e("Navigation", "Error: Doctor ID is empty!")
                            }
                        }) {
                            Text("Book Now")
                        }

                            Button(onClick = { Log.d("DoctorDetails", "Call clicked") }) {
                                Text("Call")
                            }
                        }
                    } ?: run {
                        // If doctor not found
                        Text(
                            text = "Doctor not found.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    )
}

// Function to fetch doctor details from Firestore
fun fetchDoctorById(doctorId: String, onDoctorFetched: (DoctorInfo?) -> Unit) {
    FirebaseFirestore.getInstance().collection("doctors").document(doctorId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val doctor = document.toObject(DoctorInfo::class.java)
                onDoctorFetched(doctor)
            } else {
                Log.e("DoctorDetails", "Doctor not found in Firestore")
                onDoctorFetched(null)
            }
        }
        .addOnFailureListener { e ->
            Log.e("DoctorDetails", "Failed to fetch doctor details: ${e.message}")
            onDoctorFetched(null)
        }
}
