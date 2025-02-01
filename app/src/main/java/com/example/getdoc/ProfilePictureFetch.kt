package com.example.getdoc

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

suspend fun fetchUsernameDynamically(firestore: FirebaseFirestore, userId: String): String {
    return try {
        val userDoc = firestore.collection("patients").document(userId).get().await()
        if (userDoc.exists()) {
            userDoc.getString("username") ?: "Unknown User"
        } else {
            val doctorDoc = firestore.collection("doctors").document(userId).get().await()
            if (doctorDoc.exists()) {
                doctorDoc.getString("username") ?: "Unknown Doctor"
            } else {
                "Unknown"
            }
        }
    } catch (e: Exception) {
        Log.e("Firestore", "❌ Failed to fetch username: ${e.message}")
        "Error fetching username"
    }
}
suspend fun fetchProfilePictureDynamically(
    client: Client,
    firestore: FirebaseFirestore,
    userId: String
): ByteArray? {
    return try {
        // First, check if the user exists in the `patients` collection
        val userDoc = firestore.collection("patients").document(userId).get().await()
        val imageUrl = if (userDoc.exists()) {
            userDoc.getString("profileImageUrl")
        } else {
            // If not found, check in the `doctors` collection
            val doctorDoc = firestore.collection("doctors").document(userId).get().await()
            doctorDoc.getString("profileImageUrl")
        }

        if (!imageUrl.isNullOrEmpty()) {
            val storage = Storage(client)
            val result = withContext(Dispatchers.IO) {
                storage.getFileDownload(bucketId = "678dd5d30039f0a22428", fileId = imageUrl)
            }
            result
        } else {
            Log.e("Firestore", "❌ Profile picture URL is empty")
            null
        }
    } catch (e: Exception) {
        Log.e("Firestore", "❌ Failed to fetch profile picture: ${e.message}")
        null
    }
}
@Composable
fun UserProfileDisplayDynamically(firestore: FirebaseFirestore, client: Client, userId: String) {
    var username by remember { mutableStateOf("Loading...") }
    var profileImage by remember { mutableStateOf<ByteArray?>(null) }

    LaunchedEffect(userId) {
        username = fetchUsernameDynamically(firestore, userId)
        profileImage = fetchProfilePictureDynamically(client, firestore, userId)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            profileImage?.let {
                Image(
                    bitmap = convertImageByteArrayToBitmap(it).asImageBitmap(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(100.dp).clip(CircleShape)
                )
            } ?: Text("No Image", color = Color.Gray)

        }

        Spacer(modifier = Modifier.height(8.dp))

        // Username Display
        Text(text = username, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
}
