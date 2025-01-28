package com.example.getdoc.ui.patient.appointment

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.getdoc.ui.patient.PatientViewModel
import com.example.getdoc.ui.patient.component.CustomAppBar
import com.example.getdoc.ui.patient.component.DoctorCard
import io.appwrite.Client
import io.appwrite.services.Storage
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
@Composable
fun DoctorDetailsScreen(
    doctorId: String,
    onBackClick: () -> Unit,
    client: Client,
    viewModel: PatientViewModel
) {
    val doctorList by viewModel.doctorList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Fetch doctor details
    LaunchedEffect(doctorId) {
        if (doctorList.isEmpty()) {
            viewModel.fetchDoctors() // Fetch all doctors if not already fetched
        }
    }

    // Find the doctor matching the ID
    val doctor = doctorList.find { it.id == doctorId }

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
                    .padding(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    doctor?.let {
                        // Doctor Information
                        Text(text = "Dr. ${it.name}", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Specialization: ${it.specialization}")
                        Text(text = "Location: ${it.location}")
                        Text(text = "Experience: ${it.experience} years")
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "About: ${it.about}")
                        Spacer(modifier = Modifier.height(16.dp))
                        // Add buttons for booking or calling
                        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = { Log.d("DoctorDetails", "Book Now clicked") }) {
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
