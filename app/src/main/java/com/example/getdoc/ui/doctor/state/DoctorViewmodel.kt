package com.example.getdoc.ui.doctor

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getdoc.ui.doctor.state.DoctorCredentialScreenUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
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
    val firestore: FirebaseFirestore
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(DoctorCredentialScreenUiState())
    val uiState: StateFlow<DoctorCredentialScreenUiState> get() = _uiState

    init {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            fetchDoctorStatus(userId)
        } else {
            Log.e("DoctorViewModel", "User ID is null, cannot fetch doctor status.")
        }
    }

    fun fetchDoctorStatus(userId: String) {
        Log.d("DoctorViewModel", "Fetching doctor status for user: $userId")

        // 🔹 Step 1: Check in "doctor_registrations" (Pending/Declined)
        firestore.collection("doctor_registrations").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val status = document.getString("status") ?: "pending"
                    val rejectionReason = document.getString("rejectionReason") ?: ""

                    _uiState.value = _uiState.value.copy(status = status, rejectionReason = rejectionReason)
                    Log.d("DoctorViewModel", "Fetched doctor status from registrations: $status, Reason: $rejectionReason")

                    // 🔹 Listen for real-time updates in "doctor_registrations"
                    listenForDoctorStatusUpdates(userId, "doctor_registrations")
                } else {
                    Log.d("DoctorViewModel", "Doctor not found in registrations, checking 'doctors' collection...")

                    // 🔹 Step 2: If not found, check in "doctors" (Approved)
                    firestore.collection("doctors").document(userId)
                        .get()
                        .addOnSuccessListener { doctorDoc ->
                            if (doctorDoc.exists()) {
                                val status = doctorDoc.getString("status") ?: "approved"
                                _uiState.value = _uiState.value.copy(status = status, rejectionReason = null)

                                Log.d("DoctorViewModel", "Fetched doctor status from doctors: $status")

                                // 🔹 Listen for real-time updates in "doctors"
                                listenForDoctorStatusUpdates(userId, "doctors")
                            } else {
                                Log.e("DoctorViewModel", "No record found in either collection.")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("DoctorViewModel", "Error fetching doctor status from doctors: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DoctorViewModel", "Error fetching doctor status: ${exception.message}")
            }
    }

    /**
     * 🔹 Real-time listener for doctor status updates
     */
    private fun listenForDoctorStatusUpdates(userId: String, collection: String) {
        firestore.collection(collection).document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("DoctorViewModel", "Error fetching real-time status from $collection: ${error.message}")
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val status = it.getString("status") ?: "pending"
                    val rejectionReason = it.getString("rejectionReason") ?: ""

                    Log.d("DoctorViewModel", "Realtime status update from $collection: $status, Reason: $rejectionReason")
                    _uiState.value = _uiState.value.copy(status = status, rejectionReason = rejectionReason)
                }
            }
    }


    /**
     * Submits the doctor's credentials for admin approval.
     */
    fun submitDoctorCredentialProfile(context: Context, licenseUri: Uri) {
        val state = _uiState.value
        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            _uiState.value = state.copy(isLoading = false, errorMessage = "User not authenticated")
            Toast.makeText(context, "Error: User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            try {
                val licenseFile = uriToFile(licenseUri, context) ?: run {
                    _uiState.value = state.copy(isLoading = false, errorMessage = "Failed to process license file")
                    Toast.makeText(context, "Error: Failed to process license file", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val storage = Storage(client)
                val inputFile = InputFile.fromFile(licenseFile)

                // Upload license file to Appwrite storage using userId as fileId
                val result = storage.createFile(
                    bucketId = "678dd5d30039f0a22428",
                    fileId = userId,
                    file = inputFile
                )

                val licenseUrl = result.id
                if (licenseUrl.isEmpty()) {
                    _uiState.value = state.copy(isLoading = false, errorMessage = "License upload failed")
                    Toast.makeText(context, "Error: License upload failed", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Prepare doctor profile data
                val doctorInfo = mapOf(
                    "id" to userId,
                    "name" to (state.name ?: ""),
                    "degree" to (state.degree ?: ""),
                    "specialization" to (state.speciality ?: ""),
                    "dob" to (state.dob ?: ""),
                    "location" to (state.address ?: ""),
                    "consultingFee" to (state.fee?.toIntOrNull() ?: 0),
                    "about" to (state.aboutYou ?: ""),
                    "licenseUrl" to licenseUrl,
                    "experience" to (state.experience?.toIntOrNull() ?: 0),
                    "status" to "pending",
                    "rating" to 0.0
                )

                // Save doctor info in Firestore
                firestore.collection("doctor_registrations").document(userId)
                    .set(doctorInfo)
                    .addOnSuccessListener {
                        _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                        Toast.makeText(context, "Profile submitted for review!", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e ->
                        _uiState.value = state.copy(isLoading = false, errorMessage = "Firestore error: ${e.localizedMessage}")
                        Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }

            } catch (e: Exception) {
                _uiState.value = state.copy(isLoading = false, errorMessage = "Upload failed: ${e.message}")
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun updateDoctorProfile(context: Context) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val state = _uiState.value

        val updatedData = mapOf(
            "name" to (state.name ?: ""),
            "degree" to (state.degree ?: ""),
            "specialization" to (state.speciality ?: ""),
            "dob" to (state.dob ?: ""),
            "location" to (state.address ?: ""),
            "consultingFee" to (state.fee?.toIntOrNull() ?: 0),
            "experience" to (state.experience?.toIntOrNull() ?: 0),
            "about" to (state.aboutYou ?: "")
        )

        firestore.collection("doctors").document(userId)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Update failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }


    /**
     * Converts Uri to a File for Appwrite storage upload.
     */
    private fun uriToFile(uri: Uri, context: Context): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            inputStream?.copyTo(FileOutputStream(tempFile))
            tempFile
        } catch (e: Exception) {
            Log.e("DoctorViewModel", "Failed to convert Uri to File: ${e.message}")
            null
        }
    }

    /**
     * Updates UI state fields dynamically.
     */
    fun updateUiState(field: String, value: String) {
        _uiState.value = when (field) {
            "name" -> _uiState.value.copy(name = value)
            "degree" -> _uiState.value.copy(degree = value)
            "speciality" -> _uiState.value.copy(speciality = value)
            "dob" -> _uiState.value.copy(dob = value)
            "address" -> _uiState.value.copy(address = value)
            "fee" -> _uiState.value.copy(fee = value)
            "experience" -> _uiState.value.copy(experience = value)
            "aboutYou" -> _uiState.value.copy(aboutYou = value)
            else -> _uiState.value
        }
    }

    fun clearForm() {
        _uiState.value = DoctorCredentialScreenUiState()
    }
}
