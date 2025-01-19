package com.example.getdoc.ui.patient

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.getdoc.data.model.PatientInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.getdoc.ui.patient.state.PatientUiState



class PatientViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(PatientUiState())
    val uiState: StateFlow<PatientUiState> get() = _uiState

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

        val patientId = firestore.collection("patients").document().id  // Generate a unique document ID
        val patientInfo = PatientInfo(
            id = patientId,
            firstName = state.firstName,
            lastName = state.lastName,
            age = state.age.toIntOrNull() ?: 0,
            gender = state.gender,
            relation = state.relation,
            dob = state.dob
        )

        firestore.collection("patients")
            .document(patientId)
            .set(patientInfo)
            .addOnSuccessListener {
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                Log.d("Firestore", "Patient profile successfully uploaded")
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage
                )
                Log.e("FirestoreError", "Error uploading document: ${e.localizedMessage}", e)
            }
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FirestoreError", "Task failed: ${task.exception?.localizedMessage}", task.exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error: ${task.exception?.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
    }

    fun clearForm() {
        _uiState.value = PatientUiState()
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
    }
}
