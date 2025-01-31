package com.example.getdoc.ui.patient.appointment

import Appointment
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.getdoc.R
import com.example.getdoc.data.model.DoctorInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    navController: NavController,
    firestore: FirebaseFirestore
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var selectedTabIndex by remember { mutableStateOf(0) }  // ✅ Manage Tab State
    var activeAppointments by remember { mutableStateOf<List<Appointment>?>(null) }
    var previousAppointments by remember { mutableStateOf<List<Appointment>?>(null) }

    // 🔹 Fetch Appointments
    LaunchedEffect(userId) {
        fetchAppointments(firestore, userId) { active, previous ->
            activeAppointments = active
            previousAppointments = previous
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Appointments") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ✅ Updated Tab Section with Local State Management
            TabSection(selectedTabIndex = selectedTabIndex, onTabChange = { index ->
                selectedTabIndex = index
            })

            // 🔹 Display Active or Previous Appointments
            when (selectedTabIndex) {
                0 -> {
                    if (activeAppointments == null) {
                        LoadingIndicator()
                    } else {
                        ActiveAppointmentsList(activeAppointments!!)
                    }
                }
                1 -> {
                    if (previousAppointments == null) {
                        LoadingIndicator()
                    } else {
                        PreviousAppointmentsList(previousAppointments!!)
                    }
                }
            }
        }
    }
}

// 🔹 Previous Appointments List (With Review Option)
@Composable
fun PreviousAppointmentsList(appointments: List<Appointment>) {
    if (appointments.isEmpty()) {
        NoAppointmentsMessage()
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(appointments) { appointment ->
                AppointmentCard(appointment, showReviewSection = true)
            }
        }
    }
}

// 🔹 No Appointments Found Message
@Composable
fun NoAppointmentsMessage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "No Appointments Found", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}
@Composable
fun AppointmentCard(appointment: Appointment, showReviewSection: Boolean) {
    val doctorId = appointment.doctorId ?: ""  // ✅ Ensure doctorId is set properly

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(
                        id = if (appointment.doctorInfo?.profileImage.isNullOrEmpty())
                            R.drawable.ic_launcher_foreground
                        else R.drawable.ic_launcher_foreground
                    ),
                    contentDescription = "Doctor Profile",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dr. ${appointment.doctorInfo?.name ?: "Unknown"}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = appointment.doctorInfo?.specialization ?: "Specialty Not Available",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(text = "Date: ${appointment.date ?: "N/A"}", fontSize = 14.sp)
                    Text(
                        text = "Status: ${appointment.status ?: "Pending"}",
                        fontSize = 14.sp,
                        color = when (appointment.status) {
                            "approved" -> Color.Green
                            "pending" -> Color.Yellow
                            "declined" -> Color.Red
                            else -> Color.Gray
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Time: ${appointment.timeSlot ?: "N/A"}", fontSize = 14.sp, color = Color.Gray)

            if (showReviewSection) {
                if (doctorId.isNotEmpty()) {
                    ReviewSection(doctorId = doctorId)  // ✅ Ensure correct doctorId is passed
                } else {
                    Log.e("Firestore", "❌ Error: Doctor ID is empty in AppointmentCard!")
                }
            }
        }
    }
}

@Composable
fun ReviewSection(doctorId: String) {
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    if (doctorId.isEmpty()) {
        Log.e("Firestore", "❌ Error: Doctor ID is empty in ReviewSection!")
        return
    }

    if (!isSubmitted) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(text = "Rate Your Doctor:", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                repeat(5) { index ->
                    IconButton(onClick = { rating = index + 1 }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star $index",
                            tint = if (index < rating) Color.Yellow else Color.Gray,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                label = { Text("Write a review...") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (reviewText.isNotEmpty() && rating > 0) {
                        submitReview(doctorId, rating, reviewText)
                        isSubmitted = true // Hide form after submission
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = reviewText.isNotEmpty() && rating > 0
            ) {
                Text(text = "Submit Review", color = Color.White)
            }
        }
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Your Review Submitted!", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star $index",
                            tint = if (index < rating) Color.Yellow else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = reviewText,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}


fun fetchAppointments(
    firestore: FirebaseFirestore,
    userId: String,
    onResult: (List<Appointment>, List<Appointment>) -> Unit
) {
    val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    firestore.collection("appointments")
        .whereEqualTo("patientId", userId)
        .get()
        .addOnSuccessListener { result ->
            val active = mutableListOf<Appointment>()
            val previous = mutableListOf<Appointment>()

            result.documents.forEach { document ->
                val appointment = document.toObject(Appointment::class.java)?.copy(id = document.id)
                val status = document.getString("status") ?: "pending"
                val appointmentDate = document.getString("date") ?: ""

                appointment?.let {
                    it.patientId = document.getString("patientId") ?: "Unknown"

                    // Fetch Doctor Info using doctorId
                    firestore.collection("doctors").document(it.doctorId)
                        .get()
                        .addOnSuccessListener { doctorDoc ->
                            val doctorInfo = doctorDoc.toObject(DoctorInfo::class.java)
                            it.doctorInfo = doctorInfo

                            // Classify into Active or Previous
                            if (status == "approved" && compareDates(appointmentDate, today) >= 0) {
                                active.add(it)
                            } else {
                                previous.add(it)
                            }

                            // Return final result
                            onResult(active, previous)
                        }
                }
            }
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error fetching appointments: ${e.message}")
            onResult(emptyList(), emptyList())
        }
}


fun compareDates(date1: String?, date2: String?): Int {
    val possibleFormats = arrayOf("dd/MM/yyyy", "dd-MM-yyyy")
    return try {
        if (date1.isNullOrEmpty() || date2.isNullOrEmpty()) return -1
        val formatter1 = SimpleDateFormat(possibleFormats[0], Locale.getDefault())
        val formatter2 = SimpleDateFormat(possibleFormats[1], Locale.getDefault())

        val d1 = try { formatter1.parse(date1) } catch (e: Exception) { formatter2.parse(date1) }
        val d2 = try { formatter1.parse(date2) } catch (e: Exception) { formatter2.parse(date2) }

        when {
            d1!!.after(d2) -> 1
            d1.before(d2) -> -1
            else -> 0
        }
    } catch (e: Exception) {
        Log.e("DateComparison", "Error parsing dates: ${e.message}")
        -1
    }
}
// 🔹 Active Appointments List
@Composable
fun ActiveAppointmentsList(appointments: List<Appointment>) {
    if (appointments.isEmpty()) {
        NoAppointmentsMessage()
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(appointments) { appointment ->
                AppointmentCard(appointment, showReviewSection = false)
            }
        }
    }
}
// 🔹 Tab Section
@Composable
fun TabSection(
    selectedTabIndex: Int,
    onTabChange: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        listOf("Active", "Previous").forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = {
                    if (selectedTabIndex != index) {  // Prevent unnecessary recomposition
                        onTabChange(index)
                    }
                },
                text = {
                    Text(
                        text = title,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            )
        }
    }
}


// 🔹 Loading Indicator
@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
fun submitReview(doctorId: String, rating: Int, reviewText: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val firestore = FirebaseFirestore.getInstance()

    if (doctorId.isEmpty()) {
        Log.e("Firestore", "❌ Error: Cannot submit review, doctorId is empty!")
        return
    }

    val reviewId = firestore.collection("reviews").document().id // Generate unique ID

    val reviewData = hashMapOf(
        "reviewId" to reviewId,
        "doctorId" to doctorId,  // 🔥 Important for filtering later
        "userId" to userId,
        "rating" to rating,
        "review" to reviewText,
        "timestamp" to System.currentTimeMillis()
    )

    firestore.collection("reviews")
        .document(reviewId)
        .set(reviewData)
        .addOnSuccessListener {
            Log.d("Firestore", "✅ Review stored successfully in 'reviews' collection!")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "❌ Failed to store review: ${e.message}")
        }
}
