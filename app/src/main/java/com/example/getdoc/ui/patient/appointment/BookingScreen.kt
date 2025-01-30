package com.example.getdoc.ui.theme.ui.patient.appointments

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.ui.patient.component.CustomAppBar
import com.example.getdoc.ui.patient.component.CustomButton
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun BookingDoctorScreen(
    doctorId: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    onProceedClick: () -> Unit,
    firestore: FirebaseFirestore
) {
    var doctor by remember { mutableStateOf<DoctorInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch Doctor Info from Firestore using doctorId
    LaunchedEffect(doctorId) {
        firestore.collection("doctors").document(doctorId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    doctor = document.toObject(DoctorInfo::class.java)
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
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                title = "Book Now",
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
                } else if (doctor != null) {
                    // ✅ Doctor's Information Section
                    Text(
                        text = "Dr. ${doctor!!.name}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Text(text = "Specialization: ${doctor!!.specialization}")
                    Text(text = "Experience: ${doctor!!.experience} years")
                    Text(text = "Consultation Fee: ৳ ${doctor!!.consultingFee}")

                    Spacer(modifier = Modifier.height(16.dp))

                    // ✅ Date Selection Section
                    Text(
                        text = "Choose A Date",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val dates = listOf("Today", "Tomorrow", "12 June", "13 June", "14 June")
                        items(dates) { date ->
                            DateButton(
                                text = date,
                                isSelected = selectedDate == date,
                                onClick = { onDateSelected(date) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ✅ Slots Available and Time Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Slots Available",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "9 Slots Available",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "4 PM - 6 PM",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ✅ Terms and Conditions
                    Text(
                        text = "Terms And Conditions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "The document governing the contractual relationship between the provider of a service and its user. On the web, this document is often also called 'Terms of Service' (ToS).",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Checkbox(
                            checked = true,
                            onCheckedChange = { /* Handle checkbox */ },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = Color.Gray,
                                checkmarkColor = Color.White
                            )
                        )
                        Text(
                            text = "Share All Previous Medical Files With The Doctor",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // ✅ Proceed Button
                    CustomButton(
                        buttonText = "Proceed",
                        onClick = onProceedClick
                    )
                } else {
                    // Show error message if doctor is not found
                    Text(
                        text = "Doctor not found!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    )
}

@Composable
fun DateButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .wrapContentWidth()
            .height(40.dp)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE0F7FA)
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color(0xFF00796B)
        )
    }
}
