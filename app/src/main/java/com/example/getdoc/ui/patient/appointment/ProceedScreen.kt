package com.example.getdoc.ui.patient.appointment

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.getdoc.ui.patient.component.CustomAppBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
@Composable
fun ProceedScreen(
    doctorId: String,
    selectedDate: String,
    selectedSlot: String,
    navController: NavController,
    firestore: FirebaseFirestore,
    onBackClick: () -> Unit
) {
    var doctor by remember { mutableStateOf<DoctorInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var patient by remember { mutableStateOf<Map<String, String>?>(null) }

    // Retrieve patient info from back stack if available
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val newPatientInfo = savedStateHandle?.get<Map<String, String>>("patientInfo")

    // Fetch doctor details
    LaunchedEffect(doctorId) {
        firestore.collection("doctors").document(doctorId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    doctor = document.toObject(DoctorInfo::class.java)
                    isLoading = false
                } else {
                    Log.e("Firestore", "Doctor not found!")
                    isLoading = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching doctor details: ${e.message}")
                isLoading = false
            }
    }

    // If new patient info is available, update patient state
    LaunchedEffect(newPatientInfo) {
        if (newPatientInfo != null) {
            patient = newPatientInfo
        }
    }

    Scaffold(
        topBar = {
            CustomAppBar(title = "Proceed", onBackClick = onBackClick)
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
                } else if (doctor != null) {
                    // Doctor Info
                    Text("Dr. ${doctor!!.name}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Specialization: ${doctor!!.specialization}")
                    Text("Experience: ${doctor!!.experience} years")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Appointment Details
                    Text("Appointment Date: $selectedDate")
                    Text("Time Slot: $selectedSlot")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Patient Details Section
                    Text("Patient Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    if (patient != null) {
                        PatientCard(patient!!)
                    } else {
                        Button(
                            onClick = {
                                navController.navigate("add_patient_screen/$doctorId/$selectedDate/$selectedSlot")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Add Patient")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Appointment Button
                    Button(
                        onClick = {
                            if (patient != null) {
                                bookAppointment(firestore, doctorId, selectedDate, selectedSlot, patient) { bookingId ->
                                    if (bookingId != null) {
                                        navController.navigate("confirm_screen/$bookingId")
                                    } else {
                                        Log.e("Appointment", "Failed to get booking ID")
                                    }
                                }
                            } else {
                                Log.e("Appointment", "No patient selected!")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Confirm Booking")
                    }

                } else {
                    Text("Doctor not found!", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    )
}

@Composable
fun PatientCard(patient: Map<String, String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Patient Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Name: ${patient["firstName"]} ${patient["lastName"]}")
            Text("Gender: ${patient["gender"]}")
            Text("Age: ${patient["age"]} years")
            Text("Relation: ${patient["relation"]}")
        }
    }
}
fun bookAppointment(
    firestore: FirebaseFirestore,
    doctorId: String,
    selectedDate: String,
    selectedSlot: String,
    patient: Map<String, String>?,
    onResult: (String?) -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    if (patient == null) {
        Log.e("Appointment", "No patient selected!")
        onResult(null)
        return
    }

    val appointmentData = hashMapOf(
        "doctorId" to doctorId,
        "patientId" to userId,
        "date" to selectedDate,
        "timeSlot" to selectedSlot,
        "status" to "approved", // ✅ Automatically setting status to "approved"
        "patientInfo" to patient.toMutableMap().apply { put("status", "approved") } // ✅ Updating patientInfo.status
    )

    firestore.collection("appointments")
        .add(appointmentData)
        .addOnSuccessListener { documentReference ->
            val bookingId = documentReference.id
            Log.d("Appointment", "Appointment booked successfully with ID: $bookingId")
            onResult(bookingId)
        }
        .addOnFailureListener { e ->
            Log.e("Appointment", "Error booking appointment: ${e.message}")
            onResult(null)
        }
}

