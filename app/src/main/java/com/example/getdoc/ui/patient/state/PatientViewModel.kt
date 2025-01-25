package com.example.getdoc.ui.patient

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.data.model.PatientUiState
import com.example.getdoc.ui.patient.state.PatientProfileUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PatientViewModel(
    private val client: Client,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientUiState())
    val uiState: StateFlow<PatientUiState> get() = _uiState

    private val _profileUiState = MutableStateFlow(PatientProfileUiState())
    val profileUiState: StateFlow<PatientProfileUiState> get() = _profileUiState

    fun submitPatientProfile() {
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

        val patientId = firestore.collection("patients").document().id
        val patientData = PatientUiState(
            firstName = state.firstName,
            lastName = state.lastName,
            age = state.age,
            gender = state.gender,
            relation = state.relation,
        )

        firestore.collection("patients")
            .document(patientId)
            .set(patientData)
            .addOnSuccessListener {
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                Log.d("Firestore", "Patient profile successfully uploaded")
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.localizedMessage)
                Log.e("FirestoreError", "Error uploading document: \${e.localizedMessage}", e)
            }
    }

    fun updateUiState(field: String, value: String) {
        _uiState.value = when (field) {
            "firstName" -> _uiState.value.copy(firstName = value)
            "lastName" -> _uiState.value.copy(lastName = value)
            "age" -> _uiState.value.copy(age = value)
            "gender" -> _uiState.value.copy(gender = value)
            "relation" -> _uiState.value.copy(relation = value)
            "dob" -> _uiState.value.copy(dob = value)
           else -> _uiState.value
        }
        _profileUiState.value = when (field) {
            "username" -> _profileUiState.value.copy(usernameInput = value)
            "profileImageUrl" -> _profileUiState.value.copy(imageUri = Uri.parse(value))
            else -> _profileUiState.value
        }
    }

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            _profileUiState.value = _profileUiState.value.copy(isLoading = true)
            try {
                val userSnapshot = firestore.collection("patients").document(userId).get().await()
                val username = userSnapshot.getString("username") ?: "Unknown"
                val imageId = userSnapshot.getString("profileImageUrl")

                _profileUiState.value = _profileUiState.value.copy(usernameInput = username)

                if (!imageId.isNullOrEmpty()) {
                    val storage = Storage(client)
                    val imageData = withContext(Dispatchers.IO) {
                        storage.getFileDownload(bucketId = "678dd5d30039f0a22428", fileId = imageId)
                    }
                    val bitmap = convertImageByteArrayToBitmap(imageData)
                    _profileUiState.value = _profileUiState.value.copy(profileImageBitmap = bitmap)
                }
            } catch (e: Exception) {
                Log.e("FirestoreError", "Error fetching user data: ${e.localizedMessage}", e)
                _profileUiState.value = _profileUiState.value.copy(errorMessage = "Failed to load profile")
            } finally {
                _profileUiState.value = _profileUiState.value.copy(isLoading = false)
            }
        }
    }

    fun uploadPatientProfile(context: Context) {

        val state = _profileUiState.value
        _profileUiState.value = state.copy(isLoading = true, errorMessage = null)


        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrEmpty()) {
            _profileUiState.value = state.copy(isLoading = false, errorMessage = "User not authenticated")
            Toast.makeText(context, "User is not authenticated!", Toast.LENGTH_LONG).show()
            return
        }

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

                    // Upload image to Appwrite
                    val result = storage.createFile(
                        bucketId = "678dd5d30039f0a22428",
                        fileId = ID.unique(),
                        file = inputFile
                    )

                    // Prepare Firestore data
                    val profileData = hashMapOf(
                        "username" to state.usernameInput,
                        "profileImageUrl" to result.id
                    )

                    firestore.collection("patients")
                        .document(userId)
                        .set(profileData)
                        .addOnSuccessListener {
                            _profileUiState.value = state.copy(isLoading = false, isSuccess = true)
                            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { e ->
                            _profileUiState.value = state.copy(isLoading = false, errorMessage = e.localizedMessage)
                            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    _profileUiState.value = state.copy(isLoading = false, errorMessage = "Image conversion failed")
                    Toast.makeText(context, "Failed to process image", Toast.LENGTH_LONG).show()
                }
            } catch (e: AppwriteException) {
                _profileUiState.value = state.copy(isLoading = false, errorMessage = e.message)
                Toast.makeText(context, "Error uploading image: ${e.message}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                _profileUiState.value = state.copy(isLoading = false, errorMessage = e.message)
                Toast.makeText(context, "Unexpected error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            inputStream?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            Log.e("PatientViewModel", "Failed to convert Uri to File: ${e.message}")
            null
        }
    }

    private fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    }
    private val _doctorList = MutableStateFlow<List<DoctorInfo>>(emptyList())
    val doctorList: StateFlow<List<DoctorInfo>> get() = _doctorList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    init {
        fetchDoctors()
    }
    private fun fetchDoctors() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("doctors").get().await()
                _doctorList.value = snapshot.documents.mapNotNull { document ->
                    val data = document.data
                    Log.d("FirestoreData", "Fetched doctor: $data")

                    // Parse document fields safely to avoid null issues
                    DoctorInfo(
                        about = data?.get("about") as? String ?: "",
                        consultingFee = (data?.get("consultingFee") as? Long)?.toInt() ?: 0,
                        degree = data?.get("degree") as? String ?: "",
                        dob = data?.get("dob") as? String ?: "",
                        experience = (data?.get("experience") as? Long)?.toInt() ?: 0,
                        location = data?.get("location") as? String ?: "",
                        name = data?.get("name") as? String ?: "",
                        profileImage = data?.get("profileImage").toString(),
                        rating = (data?.get("rating") as? Double) ?: 0.0,
                        specialization = data?.get("specialization") as? String ?: ""
                    )
                }
            } catch (e: Exception) {
                Log.e("Firestore", "Error fetching doctor data: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    }



