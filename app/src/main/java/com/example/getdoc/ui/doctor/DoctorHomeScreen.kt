package com.example.getdoc.ui.doctor

import Appointment
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.getdoc.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorHomeScreen(
    firestore: FirebaseFirestore,
    navController: NavHostController
) {
    var isCalendarVisible by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    val doctorId = FirebaseAuth.getInstance().currentUser?.uid

    // Date Picker State
    val appointmentDatePickerState = rememberDatePickerState()

    // Fetch all appointments on screen load or status change
    LaunchedEffect(doctorId, selectedDate) {
        doctorId?.let {
            fetchAppointments(firestore, it, selectedDate) { fetchedAppointments ->
                appointments = fetchedAppointments
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F7))
            .padding(16.dp)
    ) {
        // Header
        Header()

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar and Appointments Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (selectedDate.isEmpty()) "All Appointments" else "Appointments for $selectedDate",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Icon(
                painter = painterResource(id = R.drawable.img_14),
                contentDescription = "Calendar Icon",
                modifier = Modifier
                    .clickable { isCalendarVisible = !isCalendarVisible }
                    .size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Show Date Picker
        AnimatedVisibility(isCalendarVisible) {
            Column {
                DatePicker(state = appointmentDatePickerState)
                Button(
                    onClick = {
                        val selectedMillis = appointmentDatePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            selectedDate = convertMillisToDate(selectedMillis)
                            fetchAppointments(firestore, doctorId ?: "", selectedDate) { fetchedAppointments ->
                                appointments = fetchedAppointments
                            }
                        }
                        isCalendarVisible = false
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Select Date")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Appointments
        if (appointments.isEmpty()) {
            Text(
                text = "No appointments available",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(appointments) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onDecline = {
                            updateAppointmentStatus(firestore, appointment.id, "Declined") {
                                fetchAppointments(firestore, doctorId ?: "", selectedDate) { updatedAppointments ->
                                    appointments = updatedAppointments
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onDecline: () -> Unit
) {
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
            Text("Status: ${appointment.patientInfo.status}", fontSize = 14.sp, color = Color(0xFF2E7D32))

            Spacer(modifier = Modifier.height(8.dp))

            // Only Decline Button (Doctors cannot approve)
            Button(
                onClick = onDecline,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Decline", color = Color.White)
            }
        }
    }
}

fun fetchAppointments(
    firestore: FirebaseFirestore,
    doctorId: String,
    date: String,
    onResult: (List<Appointment>) -> Unit
) {
    val query = if (date.isEmpty()) {
        firestore.collection("appointments")
            .whereEqualTo("doctorId", doctorId)
            .whereIn("patientInfo.status", listOf("approved", "pending"))
    } else {
        firestore.collection("appointments")
            .whereEqualTo("doctorId", doctorId)
            .whereEqualTo("date", date)
            .whereIn("patientInfo.status", listOf("approved", "pending"))
    }

    query.get()
        .addOnSuccessListener { result ->
            val appointments = result.documents.mapNotNull { document ->
                try {
                    val appointment = document.toObject(Appointment::class.java)?.copy(id = document.id)

                    // ✅ Extract patientInfo manually (to avoid Firestore conversion error)
                    val patientData = document.get("patientInfo") as? Map<String, Any>
                    val patientInfo = patientData?.let {
                        PatientInfo(
                            firstName = it["firstName"] as? String ?: "",
                            lastName = it["lastName"] as? String ?: "",
                            gender = it["gender"] as? String ?: "",
                            age = it["age"]?.toString() ?: "0",  // ✅ Ensure age is converted properly
                            relation = it["relation"] as? String ?: "",
                            status = it["status"] as? String ?: "pending"
                        )
                    } ?: PatientInfo()

                    appointment?.copy(patientInfo = patientInfo)
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


fun updateAppointmentStatus(
    firestore: FirebaseFirestore,
    appointmentId: String,
    newStatus: String,
    function: () -> Unit
) {
    firestore.collection("appointments").document(appointmentId)
        .update("patientInfo.status", newStatus) // ✅ Update inside patientInfo
        .addOnSuccessListener {
            Log.d("Firestore", "Appointment status updated to $newStatus")
            function()
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Failed to update status: ${e.message}")
        }
}


data class PatientInfo(
    val firstName: String = "",
    val lastName: String = "",
    val gender: String = "",
    val age: String = "0",  // ✅ Changed from Int to String to match Firestore
    val relation: String = "",
    val status: String = "approved"
)




@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Doctor Home", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

fun convertMillisToDate(millis: Long): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return dateFormat.format(Date(millis))
}
