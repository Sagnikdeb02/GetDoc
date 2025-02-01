package com.example.getdoc.ui.doctor.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.getdoc.ui.doctor.DoctorViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun ProfileUpdateScreen(
    viewModel: DoctorViewModel,
    client: Client,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var username by remember { mutableStateOf(uiState.name ?: "") }
    var userRole = remember { mutableStateOf("LOADING...") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> profileImageUri = uri }

    LaunchedEffect(Unit) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return@LaunchedEffect
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        val firestore = viewModel.firestore

        firestore.collection("users").document(userEmail)  // ✅ Fetch role using email
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("ProfileUpdate", "User Data: ${document.data}")
                    val fetchedRole = document.getString("role")?.uppercase()?.trim()
                    if (!fetchedRole.isNullOrEmpty()) {
                        userRole.value = fetchedRole
                        Log.d("ProfileUpdate", "Role Found in USERS: $fetchedRole")
                    } else {
                        Log.e("ProfileUpdate", "Role not found in USERS! Checking DOCTORS collection.")
                        checkDoctorCollection(userId, firestore, userRole)
                    }
                } else {
                    Log.e("ProfileUpdate", "User document does not exist in USERS! Checking DOCTORS collection.")
                    checkDoctorCollection(userId, firestore, userRole)
                }
            }
            .addOnFailureListener { e ->
                Log.e("ProfileUpdate", "Failed to fetch role from USERS: ${e.message}")
                checkDoctorCollection(userId, firestore, userRole)
            }
    }

    Column(
        modifier = modifier.fillMaxSize().background(Color(0xFFF8F9FA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Role: ${userRole.value}", fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(8.dp))

        // Profile Image
        Box(
            modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (profileImageUri != null) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                )
            } else {
                Text("Select Image", color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Upload Profile Picture")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Username Field
        FormFieldComponent("Username", username) { username = it }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                coroutineScope.launch {
                    uploadProfileData(username, profileImageUri, client, viewModel, context, userRole.value)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Text("Save Profile")
        }
    }
}

// 🔹 Fetch Role from "doctors" if not found in "users"
private fun checkDoctorCollection(userId: String, firestore: FirebaseFirestore, userRole: MutableState<String>) {
    firestore.collection("doctors").document(userId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                Log.d("ProfileUpdate", "Doctor Data: ${document.data}")
                val fetchedRole = document.getString("role")?.uppercase()?.trim() ?: "DOCTOR"
                userRole.value = fetchedRole
                Log.d("ProfileUpdate", "Role Found in DOCTORS: $fetchedRole")
            } else {
                Log.e("ProfileUpdate", "User document does not exist in DOCTORS either!")
                userRole.value = "UNKNOWN"
            }
        }
        .addOnFailureListener { e ->
            Log.e("ProfileUpdate", "Failed to fetch role from DOCTORS: ${e.message}")
            userRole.value = "ERROR"
        }
}

private suspend fun uploadProfileData(
    username: String,
    imageUri: Uri?,
    client: Client,
    viewModel: DoctorViewModel,
    context: Context,
    userRole: String
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val firestore = viewModel.firestore
    val storage = Storage(client)
    var imageUrl = ""

    if (imageUri != null) {
        try {
            val imageFile = uriToFile(imageUri, context)
            if (imageFile != null) {
                val inputFile = InputFile.fromFile(imageFile)
                val result = storage.createFile(
                    bucketId = "678dd5d30039f0a22428",
                    fileId = ID.unique(),
                    file = inputFile
                )
                imageUrl = result.id
            }
        } catch (e: Exception) {
            Log.e("ProfileUpdate", "Image upload failed: ${e.message}")
        }
    }

    val collectionPath = if (userRole == "DOCTOR") "doctors" else "patients"
    val profileData = hashMapOf(
        "username" to username,
        "profileImageUrl" to imageUrl,
        "role" to userRole
    )

    // ✅ Preserve old data: Use `.update()` instead of `.set()`
    firestore.collection(collectionPath).document(userId)
        .update(profileData as Map<String, Any>)
        .addOnSuccessListener {
            Log.d("ProfileUpdate", "Profile updated successfully in $collectionPath collection!")
            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener { e ->
            Log.e("ProfileUpdate", "Profile update failed: ${e.message}")
            // ✅ If the document doesn't exist, create it without overwriting existing data
            firestore.collection(collectionPath).document(userId)
                .set(profileData, SetOptions.merge())  // ✅ Merge data instead of replacing
                .addOnSuccessListener {
                    Log.d("ProfileUpdate", "Profile created successfully in $collectionPath collection!")
                }
                .addOnFailureListener { error ->
                    Log.e("ProfileUpdate", "Final attempt failed: ${error.message}")
                }
        }
}

private fun uriToFile(uri: Uri, context: Context): File? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("profile_upload", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        tempFile
    } catch (e: Exception) {
        Log.e("ProfileUpdate", "Failed to convert Uri to File: ${e.message}")
        null
    }
}
