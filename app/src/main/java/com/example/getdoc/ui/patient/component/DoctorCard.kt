package com.example.getdoc.ui.patient.component

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.fetchProfilePictureDynamically
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DoctorCard(
    doctor: DoctorInfo,
    client: Client,
    firestore: FirebaseFirestore,
    bucketId: String,
    navController: NavController
) {
    var imageData by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch Doctor's Profile Picture
    LaunchedEffect(doctor.userId) {
        isLoading = true
        try {
            Log.d("DoctorCard", "Fetching profile image for Doctor ID: ${doctor.userId}")

            val fetchedImage = withContext(Dispatchers.IO) {
                fetchProfilePictureDynamically(client, firestore, doctor.userId)
            }

            if (fetchedImage != null) {
                imageData = fetchedImage
                Log.d("DoctorCard", "✅ Profile image loaded successfully")
            } else {
                Log.e("DoctorCard", "⚠️ Profile image is null for ${doctor.userId}")
                errorMessage = "No image found"
            }
        } catch (e: Exception) {
            Log.e("DoctorCard", "❌ Error fetching doctor image: ${e.localizedMessage}")
            errorMessage = "Error loading image"
        } finally {
            isLoading = false
        }
    }

    // Classy UI Design
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp)
            .clickable {
                val encodedDoctorId = URLEncoder.encode(doctor.userId, StandardCharsets.UTF_8.toString())
                navController.navigate("doctor_details/$encodedDoctorId")
            },
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

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Image Section
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> CircularProgressIndicator()
                        imageData != null -> {
                            Image(
                                bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData!!.size).asImageBitmap(),
                                contentDescription = "Doctor Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                            )
                        }
                        else -> {
                            Log.e("DoctorCard", "🚨 No profile image available for ${doctor.userId}")
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Profile",
                                modifier = Modifier.size(50.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Doctor Information
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dr. ${doctor.name}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = doctor.specialization,
                        fontSize = 14.sp,
                        color = Color.DarkGray, // Make the text blue

                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = "Location",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(16.dp) // Adjust the size as needed
                        )
                        Spacer(modifier = Modifier.width(4.dp)) // Add spacing between icon and text
                        Text(
                            text = doctor.location,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis // Add "..." if the text is too long
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(), // Ensures the row takes full width
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = doctor.degree,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "৳ ${doctor.consultingFee}",
                            fontSize = 14.sp,
                            color = Color.Blue, // Make the text blue
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp)) // Clip instead of background for better handling
                                .background(Color(0xFFE3F2FD)) // Light blue background with rounded edges
                                .padding(horizontal = 8.dp, vertical = 4.dp) // Add padding around the text
                        )
                    }

                }
            }
        }
    }
}





