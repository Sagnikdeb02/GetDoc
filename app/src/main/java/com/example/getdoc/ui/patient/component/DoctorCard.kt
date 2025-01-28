package com.example.getdoc.ui.patient.component

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavHostController
import com.example.getdoc.R
import com.example.getdoc.data.model.DoctorInfo
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
@Composable
fun DoctorCard(
    doctor: DoctorInfo,
    storage: Storage,
    bucketId: String,
    navController: NavHostController // Pass NavHostController here
) {
    var imageData by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(doctor.profileImage) {
        isLoading = true
        try {
            val imageId = doctor.profileImage.trim()
            if (imageId.isNotEmpty()) {
                val result = withContext(Dispatchers.IO) {
                    storage.getFileDownload(bucketId = bucketId, fileId = imageId)
                }
                imageData = result
            } else {
                Log.e("ImageFetch", "Empty or invalid image ID: ${doctor.profileImage}")
            }
        } catch (e: Exception) {
            Log.e("DoctorCard", "Error loading image: ${e.localizedMessage}")
        } finally {
            isLoading = false
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                try {
                    val encodedDoctorId = URLEncoder.encode(doctor.id, StandardCharsets.UTF_8.toString())
                    navController.navigate("DoctorDetailsScreen/$encodedDoctorId")
                } catch (e: Exception) {
                    Log.e("Navigation", "Error encoding doctor ID: ${e.localizedMessage}")
                }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        // Card Content (Keep as-is)
        Row(modifier = Modifier.padding(16.dp)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(60.dp))
            } else {
                if (imageData != null) {
                    Image(
                        bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData!!.size).asImageBitmap(),
                        contentDescription = "Doctor Image",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.doctors),
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Dr ${doctor.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = doctor.specialization, fontSize = 14.sp, color = Color.Gray)
                Text(text = doctor.location, fontSize = 12.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.star),
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700)
                    )
                    Text(text = "${doctor.rating} Rating", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Consulting Fee",
                        color = Color.Blue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "৳ ${doctor.consultingFee}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
