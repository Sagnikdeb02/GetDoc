package com.example.getdoc.ui.authentication.doctor_registration

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import android.util.Log
import androidx.navigation.NavHostController
import com.example.getdoc.navigation.AdminHomeScreen
import com.example.getdoc.navigation.LoginScreen
import com.example.getdoc.ui.authentication.AuthenticationViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@SuppressLint("RememberReturnType")
@Composable
fun AdminHomeScreen(
    navController: NavHostController,
    viewModel: AuthenticationViewModel,
    firestore: FirebaseFirestore,
    client: Client,
    bucketId: String = "678dd5d30039f0a22428") {
    val context = LocalContext.current.applicationContext

    // Ensure init is called once
    remember {
        viewModel.init(context)
    }
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
        Text("Admin Panel", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontSize = 24.sp)

        val coroutineScope = rememberCoroutineScope()
        Button(onClick = {
            coroutineScope.launch {
                viewModel.logout()
                navController.navigate(LoginScreen) {
                    popUpTo(AdminHomeScreen) { inclusive = true }
                }
            }
        }) {
            Text("Logout")
        }



        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            Button(onClick = fetchDoctorRegistrations, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Retry")
            }
        } else if (doctorList.isEmpty()) {
            Text("No doctor registrations found.", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(doctorList) { doctor ->
                    DoctorCard(firestore, client, bucketId, doctor, onStatusUpdated = {
                        fetchDoctorRegistrations()
                    })
                }
            }
        }
    }
}

@Composable
fun DoctorCard(
    firestore: FirebaseFirestore,
    client: Client,
    bucketId: String,
    doctor: Map<String, Any>,
    onStatusUpdated: () -> Unit
) {
    val context = LocalContext.current
    var imageData by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("") }

    val storage = Storage(client)

    LaunchedEffect(doctor["licenseImageId"].toString()) {
        isLoading = true
        try {
            val result = withContext(Dispatchers.IO) {
                storage.getFileDownload(bucketId, doctor["licenseImageId"].toString())
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
                        contentDescription = doctor["name"].toString(),
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
            Text("Status: ${doctor["status"]}", fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Action") },
            text = { Text("Are you sure you want to $selectedStatus this doctor?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    updateDoctorStatus(firestore, doctor["id"].toString(), selectedStatus) {
                        Toast.makeText(context, "Doctor ${selectedStatus}d successfully!", Toast.LENGTH_SHORT).show()
                        onStatusUpdated()
                    }
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

fun updateDoctorStatus(firestore: FirebaseFirestore, doctorId: String, status: String, onStatusUpdated: () -> Unit) {
    firestore.collection("doctor_registrations").document(doctorId)
        .update("status", status)
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
