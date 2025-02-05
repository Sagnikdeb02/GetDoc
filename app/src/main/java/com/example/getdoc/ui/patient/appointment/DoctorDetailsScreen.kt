package com.example.getdoc.ui.patient.appointment

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.getdoc.R
import com.example.getdoc.convertImageByteArrayToBitmap
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.fetchProfilePictureDynamically
import com.example.getdoc.theme.backgroundColor
import com.example.getdoc.theme.homeTabBackground
import com.example.getdoc.ui.patient.PatientViewModel
import com.example.getdoc.ui.patient.component.CustomAppBar
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import kotlinx.coroutines.launch

@Composable
fun DoctorDetailsScreen(
    navController: NavController,
    doctorId: String,
    onBackClick: () -> Unit,
    viewModel: PatientViewModel,
    firestore: FirebaseFirestore,
    client: Client
) {
    val coroutineScope = rememberCoroutineScope()
    var doctor by remember { mutableStateOf<DoctorInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var profileImage by remember { mutableStateOf<ByteArray?>(null) }

    // Fetch doctor details and profile picture
    LaunchedEffect(doctorId) {
        coroutineScope.launch {
            fetchDoctorById(doctorId) { fetchedDoctor ->
                doctor = fetchedDoctor
                isLoading = false
            }
            profileImage = fetchProfilePictureDynamically(client, firestore, doctorId)
        }
    }

    var reviews by remember { mutableStateOf(listOf<Review>()) }

    LaunchedEffect(doctorId) {
        val fetchedReviews = fetchDoctorReviews(doctorId) // Fetch reviews
        reviews = fetchedReviews // Update state with fetched reviews
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
                    .padding(top = paddingValues.calculateTopPadding() / 2),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    doctor?.let {
                        
                        Spacer(modifier = Modifier.height(28.dp))


                        // Doctor Information
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)) // Apply rounded corners
                                .background(Color(0xFFEAF9F6)) // Apply background color
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top, // Align items at the top
                            horizontalArrangement = Arrangement.SpaceBetween // Space between image and details
                        ) {
                            // Left side: Image
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                profileImage?.let {
                                    Image(
                                        bitmap = convertImageByteArrayToBitmap(it).asImageBitmap(),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.size(80.dp).clip(CircleShape)
                                    )
                                } ?: Text("No Image", color = Color.Gray)
                            }

                            Spacer(modifier = Modifier.width(16.dp)) // Space between image and details

                            // Right side: Details and buttons
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth() // Use remaining space
                                    .weight(1f)
                            ) {
                                // Doctor's name
                                Text(text = "Dr. ${it.name}", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(16.dp))

                                // Specialization
                                Text(
                                    text = "Specialization: ${it.specialization}",
                                    fontSize = 14.sp,
                                    color = Color.Blue,
                                    modifier = Modifier
                                        .background(Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Degree
                                Text(text = it.degree)
                                Spacer(modifier = Modifier.height(8.dp))

                                // Experience
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.experience),
                                        contentDescription = "Experience Badge",
                                        tint = Color(0xFF00C853),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${it.experience} Years of experience",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                // Location and Consulting Fee
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.LocationOn,
                                            contentDescription = "Location",
                                            tint = Color.DarkGray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = it.location,
                                            fontSize = 14.sp,
                                            color = Color.Gray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Text(
                                        text = "৳ ${it.consultingFee}",
                                        fontSize = 14.sp,
                                        color = Color.Blue,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFE3F2FD))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Buttons (aligned to the right)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween // Align buttons to the right
                                ) {

                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (doctorId.isNotEmpty()) {
                                                Log.d("Navigation", "Navigating to booking_screen/$doctorId")
                                                navController.navigate("booking_screen/$doctorId")
                                            } else {
                                                Log.e("Navigation", "Error: Doctor ID is empty!")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF008080),
                                            contentColor = Color.White)
                                    ) {
                                        Text("Book")
                                    }
                                }
                            }
                        }




                        Spacer(modifier = Modifier.height(9.dp))
                        Text(
                            text = "About",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp) // Make the Text fill the available width
                        )


                        Text(
                            text = it.about,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp), // Make the Text fill the available width
                            style = MaterialTheme.typography.bodyMedium // Optional: Apply a text style
                        )

                        Spacer(modifier = Modifier.height(16.dp))





                        DoctorReviewsScreen(doctorId)
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
