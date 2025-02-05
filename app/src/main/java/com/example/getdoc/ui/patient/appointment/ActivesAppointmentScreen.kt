package com.example.getdoc.ui.patient.appointment

import Appointment
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.navigation.NavController
import com.example.getdoc.R
import com.example.getdoc.UserProfileDisplayDynamically
import com.example.getdoc.convertImageByteArrayToBitmap
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.fetchProfilePictureDynamically
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AppointmentsScreen(
    navController: NavController,
    firestore: FirebaseFirestore,
    client: Client
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var selectedTabIndex by remember { mutableStateOf(0) }  // Manage Tab State
    var activeAppointments by remember { mutableStateOf<List<Appointment>?>(null) }
    var previousAppointments by remember { mutableStateOf<List<Appointment>?>(null) }
    var declinedAppointments by remember { mutableStateOf<List<Appointment>?>(null) }

    LaunchedEffect(userId) {
        fetchAppointments(firestore, userId) { active, previous, declined ->
            activeAppointments = active
            previousAppointments = previous
            declinedAppointments = declined
        }
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(text = "My Appointments", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TabSection(selectedTabIndex = selectedTabIndex, onTabChange = { index -> selectedTabIndex = index })

            when (selectedTabIndex) {
                0 -> if (activeAppointments == null) LoadingIndicator() else ActiveAppointmentsList(activeAppointments!!, client)
                1 -> if (previousAppointments == null) LoadingIndicator() else PreviousAppointmentsList(previousAppointments!!, client)
                2 -> if (declinedAppointments == null) LoadingIndicator() else DeclinedAppointmentsList(declinedAppointments!!, client)
            }
        }
    }
}
// 🔹 Previous Appointments List (With Review Option)
@Composable
fun PreviousAppointmentsList(appointments: List<Appointment>,client: Client) {
    if (appointments.isEmpty()) {
        NoAppointmentsMessage()
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(appointments) { appointment ->
                AppointmentCard(
                    appointment, showReviewSection = true,
                    firestore = FirebaseFirestore.getInstance(),
                    client = client
                )
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
fun AppointmentCard(appointment: Appointment, showReviewSection: Boolean, firestore: FirebaseFirestore, client: Client) {
    val doctorId = appointment.doctorId ?: ""  // ✅ Ensure doctorId is set properly

    var doctorProfileImage by remember { mutableStateOf<ByteArray?>(null) }

    // Fetch the doctor's profile picture dynamically
    LaunchedEffect(doctorId) {
        doctorProfileImage = fetchProfilePictureDynamically(client, firestore, doctorId)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
                            Color(0xFFF0F7FB) // Light background for effect
                        )
                    ),
                    shape = RoundedCornerShape(16.dp) // Match the card's shape
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    // 🔹 Display the Doctor's Profile Picture
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        doctorProfileImage?.let {
                            Image(
                                bitmap = convertImageByteArrayToBitmap(it).asImageBitmap(),
                                contentDescription = "Doctor Profile Picture",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                            )
                        } ?: Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Fallback image
                            contentDescription = "Default Doctor Image",
                            modifier = Modifier.size(60.dp).clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dr. ${appointment.doctorInfo?.name ?: "Unknown"}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = appointment.doctorInfo?.specialization ?: "Specialty Not Available",
                            fontSize = 14.sp,
                            color = Color.Blue,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE3F2FD))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Calendar Icon",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = appointment.date ?: "N/A",
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.clock),
                                    contentDescription = "Time",
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Time: ${appointment.timeSlot ?: "N/A"}",
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

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
                }

                if (showReviewSection) {
                    if (doctorId.isNotEmpty()) {
                        ReviewSection(doctorId = doctorId)
                    } else {
                        Log.e("Firestore", "❌ Error: Doctor ID is empty in AppointmentCard!")
                    }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                repeat(5) { index ->
                    IconButton(onClick = { rating = index + 1 }) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "Star $index",
                            tint = if (index < rating) Color(0xFFFFD700) else Color(0xFFEDEDED),
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
                maxLines = 3,
                shape = RoundedCornerShape(30.dp) // Apply rounded corners
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDCD4EC), // Active state color
                    contentColor = Color.DarkGray, // Active state text color
                    disabledContainerColor = Color(0xFFEDEDED), // Custom disabled background color
                    disabledContentColor = Color.Gray), // Custom disabled text color
                onClick = {
                    if (reviewText.isNotEmpty() && rating > 0) {
                        submitReview(doctorId, rating, reviewText)
                        isSubmitted = true // Hide form after submission
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = reviewText.isNotEmpty() && rating > 0
            ) {
                Text(text = "Submit Review", color = Color.DarkGray)
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
@Composable
fun DeclinedAppointmentsList(appointments: List<Appointment>, client: Client) {
    if (appointments.isEmpty()) {
        NoAppointmentsMessage()
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(appointments) { appointment ->
                AppointmentCard(appointment, showReviewSection = false, firestore = FirebaseFirestore.getInstance(), client = client)
            }
        }
    }
}
fun fetchAppointments(
    firestore: FirebaseFirestore,
    userId: String,
    onResult: (List<Appointment>, List<Appointment>, List<Appointment>) -> Unit
) {
    val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    firestore.collection("appointments")
        .whereEqualTo("patientId", userId)
        .get()
        .addOnSuccessListener { result ->
            val active = mutableListOf<Appointment>()
            val previous = mutableListOf<Appointment>()
            val declined = mutableListOf<Appointment>()

            result.documents.forEach { document ->
                val appointment = document.toObject(Appointment::class.java)?.copy(id = document.id)
                val status = document.getString("status") ?: "pending"
                val appointmentDate = document.getString("date") ?: ""

                appointment?.let {
                    it.patientId = document.getString("patientId") ?: "Unknown"

                    firestore.collection("doctors").document(it.doctorId)
                        .get()
                        .addOnSuccessListener { doctorDoc ->
                            val doctorInfo = doctorDoc.toObject(DoctorInfo::class.java)
                            it.doctorInfo = doctorInfo

                            when {
                                status == "approved" && compareDates(appointmentDate, today) >= 0 -> active.add(it)
                                status == "approved" && compareDates(appointmentDate, today) < 0 -> previous.add(it)
                                status == "declined" -> declined.add(it)
                            }
                            onResult(active, previous, declined)
                        }
                }
            }
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error fetching appointments: ${e.message}")
            onResult(emptyList(), emptyList(), emptyList())
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
fun ActiveAppointmentsList(appointments: List<Appointment>, client: Client) {
    if (appointments.isEmpty()) {
        NoAppointmentsMessage()
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(appointments) { appointment ->
                AppointmentCard(
                    appointment, showReviewSection = false,
                    firestore = FirebaseFirestore.getInstance(),
                    client = client
                )
            }

            // 🔹 Extra space at the bottom to prevent shadow effect cutoff
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun TabSection(
    selectedTabIndex: Int,
    onTabChange: (Int) -> Unit
) {
    Surface {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Active", "Previous", "Declined").forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Box(
                    modifier = Modifier.weight(1f).clickable { onTabChange(index) }.height(45.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = title, color = if (isSelected) Color(0xFF6200EE) else Color.Black)
                    if (isSelected) {
                        Box(
                            modifier = Modifier.align(Alignment.BottomCenter).height(2.dp).width(40.dp).background(Color(0xFF6200EE))
                        )
                    }
                }
            }
        }
    }
}




// 🔹 Loading Indicator
@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "No Appointments Found", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
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
