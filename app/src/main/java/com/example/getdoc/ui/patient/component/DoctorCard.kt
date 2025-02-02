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
            .padding(12.dp)
            .clickable {
                val encodedDoctorId = URLEncoder.encode(doctor.userId, StandardCharsets.UTF_8.toString())
                navController.navigate("doctor_details/$encodedDoctorId")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
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
                Text(
                    text = doctor.specialization,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = doctor.location,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.star),
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${doctor.rating} Rating",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "৳ ${doctor.consultingFee}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF388E3C) // Dark Green
                )
            }
        }
    }
}
