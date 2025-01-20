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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
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

    fun submitDoctorProfile() {
        val state = _uiState.value
        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _uiState.value = state.copy(
                isLoading = false,
                errorMessage = "User is not authenticated"
            )
            Log.e("FirebaseAuth", "User not logged in")
            return
        }

        val experienceValue = state.dob.takeIf { it.length == 10 && it.contains("-") }
            ?.split("-")?.firstOrNull()?.toIntOrNull()?.let { 2025 - it } ?: 0
        val feeValue = state.fee.toIntOrNull() ?: 0

        val doctorInfo = DoctorInfo(
            name = state.name,
            degree = state.degree,
            specialization = state.speciality,
            dob = state.dob,
            experience = experienceValue,
            location = state.address,
            consultingFee = feeValue,
            about = state.aboutYou
        )

        firestore.collection("doctors")
            .document(userId)
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

    fun uploadDoctorProfile(context: Context) {
        val state = _profileUiState.value
        _profileUiState.value = state.copy(isLoading = true, errorMessage = null)

        if (state.usernameInput.isEmpty() || state.imageUri == null) {
            _profileUiState.value = state.copy(isLoading = false, errorMessage = "Please provide all details")
            Toast.makeText(context, "Please enter username and select an image", Toast.LENGTH_LONG).show()
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

                    val profileData = hashMapOf(
                        "username" to state.usernameInput,
                        "profileImageUrl" to result.id
                    )

                    firestore.collection("doctors")
                        .add(profileData) // Allow auto-generated document ID instead of user ID
                        .addOnSuccessListener {
                            _profileUiState.value = state.copy(isLoading = false, isSuccess = true)
                            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { e ->
                            _profileUiState.value = state.copy(isLoading = false, errorMessage = e.localizedMessage)
                            Log.e("FirestoreError", "Error uploading document: ${e.localizedMessage}", e)
                        }
                }
            } catch (e: Exception) {
                _profileUiState.value = state.copy(isLoading = false, errorMessage = e.message)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Function to reset the form fields
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
