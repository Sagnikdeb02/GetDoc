package com.example.getdoc.ui.doctor

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.ui.doctor.state.DoctorProfileUiState
import com.example.getdoc.ui.doctor.state.DoctorUiState
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
class DoctorViewModel(
    private val client: Client,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorUiState())
    val uiState: StateFlow<DoctorUiState> get() = _uiState

    private val _profileUiState = MutableStateFlow(DoctorProfileUiState())
    val profileUiState: StateFlow<DoctorProfileUiState> get() = _profileUiState

    fun submitDoctorCredentialProfile() {
        val state = _uiState.value
        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        val experienceValue = state.dob.takeIf { it.length == 10 && it.contains("-") }
            ?.split("-")?.firstOrNull()?.toIntOrNull()?.let { 2025 - it } ?: 0
        val feeValue = state.fee.toIntOrNull() ?: 0

        // Generate a valid document ID (NO SPACES OR SPECIAL CHARACTERS)
        val uniqueId = state.name.replace(" ", "_") + "_" + state.dob.replace("/", "_")

        val doctorInfo = mapOf(
            "id" to uniqueId,
            "name" to state.name,
            "degree" to state.degree,
            "specialization" to state.speciality,
            "dob" to state.dob,
            "experience" to experienceValue,
            "location" to state.address,
            "consultingFee" to feeValue,
            "about" to state.aboutYou,
            "rating" to 0.0
        )

        firestore.collection("doctors")
            .document(uniqueId)
            .set(doctorInfo)
            .addOnSuccessListener {
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                Log.d("Firestore", "Doctor profile successfully uploaded")
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.localizedMessage)
                Log.e("FirestoreError", "Error uploading document: ${e.localizedMessage}", e)
            }
    }

    fun updateUiState(field: String, value: String) {
        _uiState.value = when (field) {
            "name" -> _uiState.value.copy(name = value)
            "degree" -> _uiState.value.copy(degree = value)
            "speciality" -> _uiState.value.copy(speciality = value)
            "dob" -> _uiState.value.copy(dob = value)
            "address" -> _uiState.value.copy(address = value)
            "fee" -> _uiState.value.copy(fee = value)
            "aboutYou" -> _uiState.value.copy(aboutYou = value)
            else -> _uiState.value
        }
        _profileUiState.value = when (field) {
            "username" -> _profileUiState.value.copy(usernameInput = value)
            "profileImageUrl" -> _profileUiState.value.copy(imageUri = Uri.parse(value))
            else -> _profileUiState.value
        }
    }

    fun uploadDoctorProfilePicture(context: Context, doctorId: String) {
        val state = _profileUiState.value
        _profileUiState.value = state.copy(isLoading = true, errorMessage = null)

        if (state.imageUri == null) {
            _profileUiState.value = state.copy(isLoading = false, errorMessage = "Please select an image")
            Toast.makeText(context, "Please select an image to upload", Toast.LENGTH_LONG).show()
            return
        }

        viewModelScope.launch {
            try {
                val imageFile = uriToFile(state.imageUri, context)
                if (imageFile != null) {
                    val storage = Storage(client)
                    val inputFile = InputFile.fromFile(imageFile)
                    val result = storage.createFile(
                        bucketId = "678e94b20023a8f92be0",
                        fileId = ID.unique(),
                        file = inputFile
                    )

                    // Update Firestore with the image reference
                    firestore.collection("doctors")
                        .document(doctorId)
                        .update("profileImage", result.id)
                        .addOnSuccessListener {
                            _profileUiState.value = state.copy(isLoading = false, isSuccess = true)
                            Toast.makeText(context, "Profile picture updated successfully!", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { e ->
                            _profileUiState.value = state.copy(isLoading = false, errorMessage = e.localizedMessage)
                            Log.e("FirestoreError", "Error updating profile picture: ${e.localizedMessage}", e)
                        }
                }
            } catch (e: Exception) {
                _profileUiState.value = state.copy(isLoading = false, errorMessage = e.message)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun clearForm() {
        _uiState.value = DoctorUiState()
    }

    private fun uriToFile(uri: Uri?, context: Context): File? {
        return try {
            val inputStream: InputStream? = uri?.let { context.contentResolver.openInputStream(it) }
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            inputStream?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            Log.e("DoctorViewModel", "Failed to convert Uri to File: ${e.message}")
            null
        }
    }


}
