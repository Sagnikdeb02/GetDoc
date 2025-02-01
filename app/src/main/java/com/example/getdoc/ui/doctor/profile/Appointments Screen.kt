package com.example.getdoc.ui.doctor.profile

import Appointment
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.getdoc.data.model.PatientInfo
import com.example.getdoc.ui.patient.appointment.compareDates
import com.example.getdoc.ui.theme.ui.patient.appointments.getTodayDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppointmentScreen(
    firestore: FirebaseFirestore,
    navController: NavHostController
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    val doctorId = FirebaseAuth.getInstance().currentUser?.uid
    val todayDate = getTodayDate()

    // Fetch appointments when the screen is loaded
    LaunchedEffect(doctorId) {
        doctorId?.let {
            fetchAppointments(firestore, it) { fetchedAppointments ->
                appointments = fetchedAppointments
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header
        Header(title = "Appointments")

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar for patient name & date
        SearchBar(
            query = searchQuery,
            onQueryChanged = { searchQuery = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Bar
        FilterBar(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filtered Appointments Logic
        val filteredAppointments = appointments.filter {
            val queryTrimmed = searchQuery.trim()

            val appointmentDate = it.date?.trim() ?: "" // Ensure it's not null

            val matchesName = it.patientInfo.firstName.contains(queryTrimmed, ignoreCase = true) ||
                    it.patientInfo.lastName.contains(queryTrimmed, ignoreCase = true)

            val matchesDate = queryTrimmed.isEmpty() || appointmentDate.contains(queryTrimmed, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "All" -> true
                "Approved" -> (it.patientInfo.status == "approved" || it.status == "approved")
                "Declined" -> (it.patientInfo.status == "declined" || it.status == "declined")
                "Previous" -> compareDates(appointmentDate, todayDate) < 0
                "Recent" -> compareDates(appointmentDate, todayDate) >= 0
                else -> false
            }

            (matchesName || matchesDate) && matchesFilter
        }

        // Display Appointments
        if (filteredAppointments.isEmpty()) {
            Text(
                text = "No appointments available",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(filteredAppointments) { appointment ->
                    AppointmentCard(appointment)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    var text by remember { mutableStateOf(query) }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it.trim() // Trim input spaces
            onQueryChanged(it.trim()) // Pass updated value
        },
        label = { Text("Search by Patient Name or Date (dd-mm-yyyy)") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = { text = ""; onQueryChanged("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Search")
                }
            }
        }
    )
}

@Composable
fun FilterBar(selectedFilter: String, onFilterSelected: (String) -> Unit) {
    val filters = listOf("All", "Approved", "Declined", "Previous", "Recent")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            Button(
                onClick = { onFilterSelected(filter) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedFilter == filter) Color(0xFF2E7D32) else Color.LightGray
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(text = filter, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Patient: ${appointment.patientInfo.firstName} ${appointment.patientInfo.lastName}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Gender: ${appointment.patientInfo.gender}", fontSize = 14.sp, color = Color.Gray)
            Text("Age: ${appointment.patientInfo.age} years", fontSize = 14.sp, color = Color.Gray)
            Text("Relation: ${appointment.patientInfo.relation}", fontSize = 14.sp, color = Color.Gray)
            Text("Date: ${appointment.date}", fontSize = 14.sp, color = Color.Gray)
            Text("Time: ${appointment.timeSlot}", fontSize = 14.sp, color = Color.Gray)

            val statusColor = when (appointment.patientInfo.status) {
                "approved" -> Color(0xFF2E7D32)
                "pending" -> Color.Yellow
                "declined" -> Color.Red
                else -> Color.Gray
            }

            Text("Status: ${appointment.patientInfo.status}", fontSize = 14.sp, color = statusColor)
        }
    }
}

fun fetchAppointments(
    firestore: FirebaseFirestore,
    doctorId: String,
    onResult: (List<Appointment>) -> Unit
) {
    firestore.collection("appointments")
        .whereEqualTo("doctorId", doctorId)
        .get()
        .addOnSuccessListener { result ->
            val appointments = result.documents.mapNotNull { document ->
                try {
                    val appointment = document.toObject(Appointment::class.java)?.copy(id = document.id)

                    val patientData = document.get("patientInfo") as? Map<String, Any>
                    val patientInfo = patientData?.let {
                        PatientInfo(
                            firstName = it["firstName"] as? String ?: "",
                            lastName = it["lastName"] as? String ?: "",
                            gender = it["gender"] as? String ?: "",
                            age = it["age"]?.toString() ?: "0",
                            relation = it["relation"] as? String ?: "",
                            status = it["status"] as? String ?: "pending"
                        )
                    } ?: PatientInfo()

                    appointment?.copy(patientInfo = patientInfo, status = document.getString("status") ?: "pending")
                } catch (e: Exception) {
                    Log.e("Firestore", "Error parsing appointment: ${e.message}")
                    null
                }
            }
            onResult(appointments)
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error fetching appointments: ${e.message}")
            onResult(emptyList())
        }
}
@Composable
fun Header(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}