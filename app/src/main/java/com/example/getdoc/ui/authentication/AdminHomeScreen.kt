package com.example.getdoc.ui.authentication

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("RememberReturnType")
@Composable
fun AdminHomeScreen(
    navController: NavHostController,
    firestore: FirebaseFirestore,
    client: Client,
    bucketId: String = "678dd5d30039f0a22428"
) {
    val coroutineScope = rememberCoroutineScope()
    var doctorList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val fetchDoctorRegistrations: () -> Unit = {
        isLoading = true
        errorMessage = null

        firestore.collection("doctor_registrations")
            .get()
            .addOnSuccessListener { documents ->
                doctorList = documents.map { document ->
                    val data = document.data.toMutableMap()
                    data["id"] = document.id
                    data
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                errorMessage = exception.message ?: "Error loading data"
                isLoading = false
                Log.e("AdminHomeScreen", "Error fetching doctor registrations: ${exception.message}")
            }
    }

    LaunchedEffect(Unit) {
        fetchDoctorRegistrations()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Admin Panel", fontWeight = FontWeight.Bold, fontSize = 24.sp)

        Spacer(modifier = Modifier.height(8.dp))

        // Logout Button
        Button(
            onClick = {
                coroutineScope.launch {
                    navController.navigate("login") {
                        popUpTo("adminHome") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = fetchDoctorRegistrations) { Text("Retry") }
            }
        } else if (doctorList.isEmpty()) {
            Text("No doctor registrations found.", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(doctorList) { doctor ->
                    DoctorStatusCard(firestore, client, bucketId, doctor, onStatusUpdated = {
                        fetchDoctorRegistrations()
                    })
                }
            }
        }
    }
}

@Composable
fun DoctorStatusCard(
    firestore: FirebaseFirestore,
    client: Client,
    bucketId: String,
    doctor: Map<String, Any>,
    onStatusUpdated: () -> Unit
) {
    val storage = io.appwrite.services.Storage(client)
    var imageData by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("") }

    val doctorId = doctor["id"].toString()
    val doctorStatus = doctor["status"].toString()
    val licenseImageId = doctor["licenseUrl"].toString()
    val context = LocalContext.current

    LaunchedEffect(licenseImageId) {
        isLoading = true
        try {
            val result = withContext(Dispatchers.IO) {
                storage.getFileDownload(bucketId, licenseImageId)
            }
            imageData = result
        } catch (e: Exception) {
            Log.e("DoctorCard", "Failed to load image: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Doctor Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Show Doctor Image (License)
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                imageData?.let {
                    Image(
                        bitmap = convertImageByteArrayToBitmap(it).asImageBitmap(),
                        contentDescription = "License Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Name: ${doctor["name"]}", fontWeight = FontWeight.Bold)
            Text("Email: ${doctor["email"]}")
            Text("Degree: ${doctor["degree"]}")
            Text("Specialization: ${doctor["specialization"]}")
            Text("Status: $doctorStatus", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            if (doctorStatus == "pending") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            selectedStatus = "approved"
                            showDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Approve")
                    }

                    Button(
                        onClick = {
                            selectedStatus = "declined"
                            showDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                    ) {
                        Text("Decline")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Action") },
            text = { Text("Are you sure you want to $selectedStatus this doctor?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    processDoctorApproval(firestore, doctor, selectedStatus, onStatusUpdated, context)
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun processDoctorApproval(
    firestore: FirebaseFirestore,
    doctor: Map<String, Any>,
    status: String,
    onStatusUpdated: () -> Unit,
    context: android.content.Context
) {
    val doctorId = doctor["id"].toString()

    if (status == "approved") {
        val approvedDoctorInfo = doctor.toMutableMap()
        approvedDoctorInfo.remove("licenseUrl") // Remove confidential license info
        approvedDoctorInfo["status"] = "approved"

        firestore.collection("doctors").document(doctorId)
            .set(approvedDoctorInfo)
            .addOnSuccessListener {
                firestore.collection("doctor_registrations").document(doctorId).delete() // Remove from pending list
                Toast.makeText(context, "Doctor approved and added to system!", Toast.LENGTH_SHORT).show()
                onStatusUpdated()
            }
            .addOnFailureListener { e ->
                Log.e("AdminHomeScreen", "Failed to approve doctor: ${e.message}")
            }
    } else {
        firestore.collection("doctor_registrations").document(doctorId)
            .update("status", "declined", "rejectionReason", "Admin review failed")
            .addOnSuccessListener { onStatusUpdated() }
    }
}

fun updateDoctorStatus(firestore: FirebaseFirestore, doctorId: String, status: String, onStatusUpdated: () -> Unit) {
    val updateData = if (status == "declined") {
        mapOf("status" to status, "rejectionReason" to "Admin review failed")
    } else {
        mapOf("status" to status)
    }

    firestore.collection("doctors").document(doctorId)
        .update(updateData)
        .addOnSuccessListener {
            Log.d("AdminHomeScreen", "Doctor status updated to $status")
            onStatusUpdated()
        }
        .addOnFailureListener { e ->
            Log.e("AdminHomeScreen", "Failed to update doctor status: ${e.message}")
        }
}
fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
}
