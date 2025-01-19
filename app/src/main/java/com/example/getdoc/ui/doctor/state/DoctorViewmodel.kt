package com.example.getdoc.ui.doctor

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.getdoc.data.model.DoctorInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.getdoc.ui.doctor.state.DoctorUiState

class DoctorViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(DoctorUiState())
    val uiState: StateFlow<DoctorUiState> get() = _uiState

    // Function to submit doctor credentials
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
            specialization = state.speciality,  // Matches with updated model
            dob = state.dob,  // Matches with updated model
            experience = experienceValue,
            location = state.address,  // Matches with updated model
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
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage
                )
                Log.e("FirestoreError", "Error uploading document: ${e.localizedMessage}", e)
            }
    }

    // Function to reset the form fields
    fun clearForm() {
        _uiState.value = DoctorUiState()
    }

    // Update state from user input
    fun updateUiState(field: String, value: String) {
        Log.d("UpdateState", "Field: $field, Value: $value")
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
    }


    // Fetch doctor profile
    fun fetchDoctorProfile(userId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        firestore.collection("doctors").document(userId).get()
            .addOnSuccessListener { document ->
                document.toObject(DoctorInfo::class.java)?.let {
                    _uiState.value = _uiState.value.copy(
                        name = it.name,
                        degree = it.degree,
                        speciality = it.specialization,
                        dob = it.dob,
                        address = it.location,
                        fee = it.consultingFee.toString(),
                        aboutYou = it.about,
                        isLoading = false
                    )
                }
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.localizedMessage)
                Log.e("FirestoreError", "Error fetching document: ${e.localizedMessage}", e)
            }
    }
}
