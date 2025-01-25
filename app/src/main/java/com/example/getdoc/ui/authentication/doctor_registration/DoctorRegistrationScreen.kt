package com.example.getdoc.ui.authentication.doctor_registration

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import android.widget.Toast
@Composable
fun DoctorRegistrationScreen(
    client: Client,
    firestore: FirebaseFirestore,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var doctorName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var registrationStatus by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> selectedImageUri = uri }
    )

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Doctor Registration", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = doctorName,
            onValueChange = { doctorName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Select License Image")
        }

        selectedImageUri?.let {
            Text(text = "Selected Image: $it")
        }

        Button(
            onClick = {
                if (doctorName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword == password && selectedImageUri != null) {
                    isSubmitting = true
                    coroutineScope.launch {
                        uploadDoctorLicense(
                            doctorName = doctorName,
                            email = email,
                            password = password,
                            imageUri = selectedImageUri!!,
                            client = client,
                            firestore = firestore,
                            context = context,
                            onSuccess = {
                                registrationStatus = "pending"
                                isSubmitting = false
                                Toast.makeText(
                                    context,
                                    "Registration submitted successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFailure = { error ->
                                isSubmitting = false
                                Toast.makeText(
                                    context,
                                    "Failed to submit registration: $error",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields correctly!", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting
        ) {
            Text(text = if (isSubmitting) "Submitting..." else "Submit for Approval")
        }

        if (registrationStatus == "pending") {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Your registration is under review!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Please wait for a few minutes while the admin verifies your credentials. If your identity is correct, you will be able to sign in within 5-10 minutes.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }}}
    }
}
suspend fun uploadDoctorLicense(
    doctorName: String,
    email: String,
    password: String,
    imageUri: Uri,
    client: Client,
    firestore: FirebaseFirestore,
    context: Context,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    try {
        // Generate a unique ID if the user is not authenticated
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            ?: firestore.collection("doctor_registrations").document().id // Generate Firestore unique ID

        val imageFile = uriToFile(imageUri, context)
        if (imageFile != null) {
            val inputFile = InputFile.fromFile(imageFile)
            val storage = Storage(client)

            val result = storage.createFile(
                bucketId = "678dd5d30039f0a22428",
                fileId = ID.unique(),
                file = inputFile
            )

            val doctorData = hashMapOf(
                "userId" to userId,
                "name" to doctorName,
                "email" to email,
                "password" to password,
                "licenseImageId" to result.id,
                "status" to "pending"
            )

            firestore.collection("doctor_registrations")
                .document(userId)
                .set(doctorData)
                .addOnSuccessListener {
                    Log.d("DoctorRegistration", "Registration submitted successfully!")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("DoctorRegistration", "Failed to submit registration: ${e.message}")
                    onFailure(e.message ?: "Unknown error")
                }
        } else {
            throw Exception("Failed to create file from Uri.")
        }
    } catch (e: AppwriteException) {
        Log.e("DoctorRegistration", "AppwriteException: ${e.message}")
        onFailure(e.message ?: "Appwrite error")
    } catch (e: Exception) {
        Log.e("DoctorRegistration", "Exception: ${e.message}")
        onFailure(e.message ?: "Unknown error")
    }
}

private fun uriToFile(uri: Uri, context: Context): File? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        tempFile
    } catch (e: Exception) {
        Log.e("DoctorRegistration", "Failed to convert Uri to File: ${e.message}")
        null
    }
}
