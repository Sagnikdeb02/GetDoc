package com.example.getdoc.ui.theme.ui.patient.appointments

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.getdoc.convertImageByteArrayToBitmap
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.fetchProfilePictureDynamically
import com.example.getdoc.ui.patient.component.CustomAppBar
import com.example.getdoc.ui.patient.component.CustomButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import java.util.Calendar

@Composable
fun BookingDoctorScreen(
    doctorId: String,
    navController: NavController,
    firestore: FirebaseFirestore,
    onBackClick: () -> Unit,client: Client
) {
    val context = LocalContext.current
    var doctor by remember { mutableStateOf<DoctorInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedDate by remember { mutableStateOf(getTodayDate()) }
    var availableSlots by remember { mutableStateOf(generateSlots()) }
    var bookedSlots by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedSlot by remember { mutableStateOf("") }
    var isSlotBooked by remember { mutableStateOf(false) }
    var profileImage by remember { mutableStateOf<ByteArray?>(null) }

    // 🔹 Fetch Doctor Info from Firestore
    LaunchedEffect(doctorId, selectedDate) {
        firestore.collection("doctors").document(doctorId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    doctor = document.toObject(DoctorInfo::class.java)
                    fetchAvailableSlots(firestore, doctorId, selectedDate) { slots, booked ->
                        availableSlots = slots
                        bookedSlots = booked
                    }
                    isLoading = false
                    Log.d("Firestore", "Doctor Info Loaded: ${doctor?.name}")
                } else {
                    Log.e("Firestore", "Doctor not found!")
                    isLoading = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching doctor details: ${e.message}")
                isLoading = false
            }
        profileImage = fetchProfilePictureDynamically(client, firestore, doctorId)

    }

    Scaffold(
        topBar = { CustomAppBar(title = "Book Appointment", onBackClick = onBackClick) },
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
                    // ✅ Doctor's Info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEDED)
                        )
                    ){
                        Column(modifier = Modifier.padding(16.dp)){
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(shape = CircleShape)
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                profileImage?.let {
                                    Image(
                                        bitmap = convertImageByteArrayToBitmap(it).asImageBitmap(),
                                        contentDescription = "Doctor Profile Picture",
                                        modifier = Modifier.size(80.dp).clip(CircleShape)
                                    )
                                } ?: Text("No Image", color = Color.Gray)
                            }

                            Text(
                                text = "Dr. ${doctor!!.name}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                            Text(text = "Specialization: ${doctor!!.specialization}")
                            Text(text = "Experience: ${doctor!!.experience} years")
                            Text(text = "Consultation Fee: ৳ ${doctor!!.consultingFee}")
                        }
                    }




                    Spacer(modifier = Modifier.height(16.dp))

                    // ✅ Date Picker
                    Text("Choose A Date", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            showDatePicker(context) { newDate ->
                                selectedDate = newDate
                                fetchAvailableSlots(firestore, doctorId, selectedDate) { slots, booked ->
                                    availableSlots = slots
                                    bookedSlots = booked
                                    isSlotBooked = bookedSlots.contains(selectedSlot)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE0F7FA),
                            contentColor = Color.DarkGray),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text("Select Date: $selectedDate")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ✅ Slot Selection
                    Text("Available Slots", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    if (availableSlots.isEmpty()) {
                        Text("No slots available", color = Color.Red)
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(availableSlots) { slot ->
                                SlotButton(
                                    text = slot,
                                    isSelected = selectedSlot == slot,
                                    isUnavailable = bookedSlots.contains(slot),
                                    onClick = {
                                        selectedSlot = slot
                                        isSlotBooked = bookedSlots.contains(slot)
                                    },
                                )
                            }
                        }
                    }

                    // 🚨 Show warning if the selected slot is booked
                    if (isSlotBooked) {
                        SlotUnavailableWarning()
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ✅ Proceed Button
                    CustomButton(
                        buttonText = "Proceed",
                        onClick = {
                            if (selectedSlot.isNotEmpty() && !isSlotBooked) {
                                saveAppointment(firestore, doctorId, selectedDate, selectedSlot)
                                navController.navigate("proceed_screen/$doctorId/$selectedDate/$selectedSlot")
                            } else {
                                Log.e("Booking", "No slot selected or slot is already booked!")
                            }
                        }
                    )
                } else {
                    Text("Doctor not found!", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    )
}

// 🔹 Show Slot Unavailable Warning
@Composable
fun SlotUnavailableWarning() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0E0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("⚠️ Slot Not Available", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Please select another time slot.", color = Color.Black)
        }
    }
}

// 🔹 Fetch available slots for a doctor on a selected date
fun fetchAvailableSlots(firestore: FirebaseFirestore, doctorId: String, selectedDate: String, onResult: (List<String>, List<String>) -> Unit) {
    firestore.collection("appointments")
        .whereEqualTo("doctorId", doctorId)
        .whereEqualTo("date", selectedDate)
        .get()
        .addOnSuccessListener { result ->
            val bookedSlots = result.documents.mapNotNull { it.getString("timeSlot") }
            val allSlots = generateSlots()
            val availableSlots = allSlots.filterNot { bookedSlots.contains(it) }

            Log.d("Firestore", "Available Slots on $selectedDate: $availableSlots")
            Log.d("Firestore", "Booked Slots on $selectedDate: $bookedSlots")

            onResult(availableSlots, bookedSlots)
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error fetching available slots: ${e.message}")
        }
}

// 🔹 Slot Button UI
@Composable
fun SlotButton(text: String, isSelected: Boolean, isUnavailable: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isUnavailable -> Color.Gray
                isSelected ->  Color(0xFF008080)
                else -> Color(0xFFE0F7FA)
            },
            contentColor = when {
                isUnavailable -> Color.DarkGray
                isSelected -> Color.White
                else -> Color.DarkGray
            }
        ),
        enabled = !isUnavailable
    ) {
        Text(text, fontSize = 14.sp, )
    }
}

// 🔹 Fetch available slots for a doctor on a selected date
fun fetchAvailableSlots(firestore: FirebaseFirestore, doctorId: String, selectedDate: String, onResult: (List<String>) -> Unit) {
    firestore.collection("appointments").document(doctorId)
        .collection(selectedDate)
        .get()
        .addOnSuccessListener { result ->
            val bookedSlots = result.documents.mapNotNull { it.id }
            Log.d("Firestore", "Booked Slots on $selectedDate: $bookedSlots") // ✅ Debugging Log

            val allSlots = generateSlots()
            val availableSlots = allSlots.filterNot { bookedSlots.contains(it) }

            Log.d("Firestore", "Available Slots on $selectedDate: $availableSlots") // ✅ Debugging Log

            onResult(availableSlots)
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error fetching available slots: ${e.message}")
        }
}

// 🔹 Save appointment to Firestore
fun saveAppointment(firestore: FirebaseFirestore, doctorId: String, selectedDate: String, selectedSlot: String) {
    val patientId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val appointmentData = hashMapOf(
        "time" to selectedSlot,
        "patientId" to patientId,
        "status" to "Booked",
        "bookingId" to "${doctorId}_$selectedSlot"
    )

    firestore.collection("appointments").document(doctorId)
        .collection(selectedDate)
        .document(selectedSlot)
        .set(appointmentData)
        .addOnSuccessListener {
            Log.d("Firestore", "Appointment booked successfully!")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Failed to book appointment: ${e.message}")
        }
}

// 🔹 Slot Button UI
@Composable
fun SlotButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE0F7FA))
    ) {
        Text(text, fontSize = 14.sp, color = if (isSelected) Color.White else Color(0xFF00796B))
    }
}

// 🔹 Show date picker
fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

// 🔹 Generate slots dynamically
fun generateSlots(): List<String> {
    val slots = mutableListOf<String>()
    var hour = 16  // Start at 4 PM
    var minute = 0
    for (i in 1..20) {
        slots.add(String.format("%02d:%02d", hour, minute))
        minute += 20
        if (minute >= 60) {
            minute = 0
            hour++
        }
    }
    return slots
}

// 🔹 Get today's date
fun getTodayDate(): String {
    val calendar = Calendar.getInstance()
    return String.format("%02d-%02d-%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
}
