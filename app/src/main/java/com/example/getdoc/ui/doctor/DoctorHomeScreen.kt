package com.example.getdoc.ui.doctor

import Appointment
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.getdoc.R
import com.example.getdoc.convertImageByteArrayToBitmap
import com.example.getdoc.data.model.PatientInfo
import com.example.getdoc.fetchProfilePictureDynamically
import com.example.getdoc.fetchUsernameDynamically
import com.example.getdoc.ui.patient.appointment.compareDates
import com.example.getdoc.ui.theme.ui.patient.appointments.getTodayDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import java.text.SimpleDateFormat
import java.util.*
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorHomeScreen(
    firestore: FirebaseFirestore,
    navController: NavHostController,
    client: Client
) {
    var isCalendarVisible by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    val doctorId = FirebaseAuth.getInstance().currentUser?.uid
    val todayDate = getTodayDate()
    var username by remember { mutableStateOf("Loading...") }
    var profileImage by remember { mutableStateOf<ByteArray?>(null) }
    var userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        username = fetchUsernameDynamically(firestore, userId)
        profileImage = fetchProfilePictureDynamically(client, firestore, userId)
    }

    LaunchedEffect(doctorId) {
        doctorId?.let {
            fetchAppointments(firestore, it) { fetchedAppointments ->
                appointments = fetchedAppointments
            }
        }
    }

    LaunchedEffect(selectedDate) {
        if (selectedDate.isNotEmpty()) {
            doctorId?.let {
                fetchAppointmentsByDate(firestore, it, selectedDate) { fetchedAppointments ->
                    appointments = fetchedAppointments
                }
            }
        } else {
            doctorId?.let {
                fetchAppointments(firestore, it) { fetchedAppointments ->
                    appointments = fetchedAppointments
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F7))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GetDoc",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF008080)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hi, $username",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    profileImage?.let {
                        Image(
                            bitmap = convertImageByteArrayToBitmap(it).asImageBitmap(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                        )
                    } ?: Icon(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Default Profile",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (selectedDate.isEmpty()) "All Appointments" else "Appointments for $selectedDate",
                style = MaterialTheme.typography.titleLarge
            )
            Icon(
                painter = painterResource(id = R.drawable.img_14),
                contentDescription = "Calendar Icon",
                modifier = Modifier.clickable { isCalendarVisible = !isCalendarVisible }.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val datePickerState = rememberDatePickerState()

        AnimatedVisibility(isCalendarVisible) {
            Column {
                DatePicker(state = datePickerState)
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = convertMillisToDate(millis)
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

        if (appointments.isEmpty()) {
            Text(
                text = if (selectedDate.isEmpty()) "No appointments available" else "No appointments on this date",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(appointments) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onDecline = {
                            updateAppointmentStatus(firestore, appointment.id, "declined") {
                                if (selectedDate.isNotEmpty()) {
                                    fetchAppointmentsByDate(firestore, doctorId ?: "", selectedDate) { updatedAppointments ->
                                        appointments = updatedAppointments
                                    }
                                } else {
                                    fetchAppointments(firestore, doctorId ?: "") { updatedAppointments ->
                                        appointments = updatedAppointments
                                    }
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

fun fetchAppointmentsByDate(
    firestore: FirebaseFirestore,
    doctorId: String,
    date: String,
    onResult: (List<Appointment>) -> Unit
) {
    firestore.collection("appointments")
        .whereEqualTo("doctorId", doctorId)
        .whereEqualTo("date", date)
        .whereEqualTo("patientInfo.status", "approved")
        .get()
        .addOnSuccessListener { result ->
            val appointments = result.documents.mapNotNull { document ->
                try {
                    document.toObject(Appointment::class.java)?.copy(id = document.id)
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



fun fetchAppointments(
    firestore: FirebaseFirestore,
    doctorId: String,
    onResult: (List<Appointment>) -> Unit
) {
    firestore.collection("appointments")
        .whereEqualTo("doctorId", doctorId)
        .whereEqualTo("patientInfo.status", "approved")
        .get()
        .addOnSuccessListener { result ->
            val appointments = result.documents.mapNotNull { document ->
                try {
                    document.toObject(Appointment::class.java)?.copy(id = document.id)
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
fun AppointmentCard(
    appointment: Appointment,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp), // Rounded corners
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp // Soft shadow
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Transparent to allow gradient background
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient( // Apply a vertical gradient
                        colors = listOf(
                            Color(0xFFFFFFFF), // Pure white
                            Color(0xFFF0F7FB) // Even lighter gray for a subtle effect
                        )
                    ),
                    shape = RoundedCornerShape(16.dp) // Match the card's shape
                )

        ){
            Row(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Patient: ${appointment.patientInfo.firstName} ${appointment.patientInfo.lastName}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Gender: ${appointment.patientInfo.gender}", fontSize = 14.sp, color = Color.Gray)
                    Text("Age: ${appointment.patientInfo.age} years", fontSize = 14.sp, color = Color.Gray)
                    Text("Relation: ${appointment.patientInfo.relation}", fontSize = 14.sp, color = Color.Gray)

                    val statusColor = Color(0xFF2E7D32) // Approved status color
                    Text("Status: ${appointment.patientInfo.status}", fontSize = 14.sp, color = statusColor)
                }

                Spacer(modifier = Modifier.width(16.dp)) // Add spacing between columns

                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Bottom) {
                    Text("Date: ${appointment.date}", fontSize = 14.sp, color = Color.Gray)
                    Text("Time: ${appointment.timeSlot}", fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Show Decline Button ONLY for Approved Appointments
                    Button(
                        onClick = onDecline,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008080)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Decline", color = Color.White)
                    }
                }
            }

        }




    }
}

// 🔹 Fetch Future Appointments, Excluding Declined Ones
fun fetchAppointments(
    firestore: FirebaseFirestore,
    doctorId: String,
    date: String,
    todayDate: String,
    onResult: (List<Appointment>) -> Unit
) {
    firestore.collection("appointments")
        .whereEqualTo("doctorId", doctorId)
        .whereIn("patientInfo.status", listOf("approved")) // ✅ Filter only approved appointments
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
                            status = it["status"] as? String ?: "approved"
                        )
                    } ?: PatientInfo()

                    if (compareDates(appointment?.date, todayDate) >= 0) {
                        appointment?.copy(patientInfo = patientInfo)
                    } else {
                        null
                    }
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
    val updateData = mapOf(
        "patientInfo.status" to newStatus,  // ✅ Update inside patientInfo
        "status" to newStatus               // ✅ Update root status field
    )

    firestore.collection("appointments").document(appointmentId)
        .update(updateData)
        .addOnSuccessListener {
            Log.d("Firestore", "Appointment status updated to $newStatus")
            function()
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Failed to update status: ${e.message}")
        }
}



fun convertMillisToDate(millis: Long): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return dateFormat.format(Date(millis))
}
@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Doctor Home",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}