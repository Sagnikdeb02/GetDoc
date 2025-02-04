package com.example.getdoc.ui.patient.appointment

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

suspend fun fetchDoctorReviews(doctorId: String): List<Review> {
    val firestore = FirebaseFirestore.getInstance()
    val reviewsList = mutableListOf<Review>()

    return try {
        val reviewDocs = firestore.collection("reviews")
            .whereEqualTo("doctorId", doctorId)
            .get()
            .await()

        for (doc in reviewDocs) {
            val review = doc.toObject(Review::class.java)

            // ✅ Fetch patient's details from "patients" instead of "users"
            val userDoc = firestore.collection("patients").document(review.userId).get().await()
            val patientName = userDoc.getString("username") ?: "Anonymous"  // ✅ Using "username"
            val patientProfile = userDoc.getString("profileImageUrl") ?: "" // ✅ Using "profileImageUrl"

            reviewsList.add(
                review.copy(
                    patientName = patientName,
                    patientProfile = patientProfile
                )
            )
        }
        Log.d("Firestore", "✅ Successfully fetched ${reviewsList.size} reviews for Doctor ID: $doctorId")
        reviewsList
    } catch (e: Exception) {
        Log.e("Firestore", "❌ Failed to fetch reviews: ${e.message}")
        emptyList()
    }
}


// 🔹 Doctor Review Screen
@Composable
fun DoctorReviewsScreen(doctorId: String) {
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(doctorId) {
        reviews = fetchDoctorReviews(doctorId)
        isLoading = false
    }

    Spacer(modifier = Modifier.height(16.dp))

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {


        Text(
            text = "Patient Reviews",
            style = MaterialTheme.typography.titleLarge
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE0E0E0)) // Light gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading) {
            // Show loading indicator while fetching reviews
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (reviews.isEmpty()) {
            // Show message if no reviews exist
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No reviews yet!",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn {
                items(reviews) { review ->
                    ReviewItem(review)
                }
            }
        }
    }
}

// 🔹 Review Card UI
@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        //elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔹 Display Patient Profile Image
            Image(
                painter = rememberAsyncImagePainter(review.patientProfile.ifEmpty { "https://via.placeholder.com/100" }),
                contentDescription = "Patient Profile",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            )

            Column(modifier = Modifier.padding(start = 12.dp)) {
                // 🔹 Display Patient's Name
                Text(
                    text = review.patientName.ifEmpty { "Anonymous" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 🔹 Display Star Ratings
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star $index",
                            tint = if (index < review.rating) Color(0xFFFFD700) else Color(0xFFEDEDED),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 🔹 Display Review Message
                Text(
                    text = review.review.ifEmpty { "No review message provided" },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.padding(16.dp))

            }
        }
    }
}

// 🔹 Review Model
data class Review(
    val reviewId: String = "",
    val doctorId: String = "",
    val userId: String = "", // Added for fetching user details
    val rating: Int = 0,
    val review: String = "",
    val patientName: String = "",
    val patientProfile: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
