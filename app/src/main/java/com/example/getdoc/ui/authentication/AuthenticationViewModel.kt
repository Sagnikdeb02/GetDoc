package com.example.getdoc.ui.authentication

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getdoc.navigation.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthenticationViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    var authUiState = MutableStateFlow(AuthUiState())
        private set

    var firebaseUser = MutableStateFlow<FirebaseUser?>(null)
    private val _isLicenseUploadComplete = MutableStateFlow(false)
    val isLicenseUploadComplete: StateFlow<Boolean> = _isLicenseUploadComplete

    init {
        firebaseUser.value = firebaseAuth.currentUser
    }

    fun onEmailChange(email: String) {
        authUiState.update {
            it.copy(email = email)
        }
    }

    fun onPasswordChange(password: String) {
        authUiState.update {
            it.copy(password = password)
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        authUiState.update {
            it.copy(confirmPassword = confirmPassword)
        }
    }

    fun onRoleChange(role: Role) {
        authUiState.update {
            it.copy(role = role)
        }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            try {
                val user = firebaseAuth.createUserWithEmailAndPassword(
                    authUiState.value.email,
                    authUiState.value.password
                ).await().user

                firebaseUser.value = user

                if (authUiState.value.role == Role.DOCTOR) {
                    // Doctor-specific registration
                    user?.let {
                        registerDoctor(it.uid, authUiState.value.email)
                    }
                }
            } catch (e: Exception) {
                firebaseUser.value = null
            }
        }
    }

    fun onSignInClick() {
        firebaseAuth.signInWithEmailAndPassword(
            authUiState.value.email,
            authUiState.value.password
        ).addOnSuccessListener {
            firebaseUser.value = it.user
        }.addOnFailureListener {
            firebaseUser.value = null
        }
    }

    fun onPasswordResetClick() {
        firebaseAuth.sendPasswordResetEmail(authUiState.value.email)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
            }
    }

    private suspend fun registerDoctor(userId: String, email: String) {
        try {
            val licenseUri = authUiState.value.licenseUri
                ?: throw Exception("License URI is missing")

            // Upload license image to Firebase Storage
            val licenseRef = storage.reference.child("doctor_licenses/$userId.jpg")
            licenseRef.putFile(licenseUri).await()

            // Get the download URL for the uploaded license
            val licenseUrl = licenseRef.downloadUrl.await().toString()

            // Save doctor details in Firestore
            val doctorData = mapOf(
                "email" to email,
                "licenseUrl" to licenseUrl,
                "isApproved" to false
            )
            firestore.collection("doctors").document(userId).set(doctorData).await()

            _isLicenseUploadComplete.value = true
        } catch (e: Exception) {
            _isLicenseUploadComplete.value = false
            throw e
        }
    }

    fun uploadLicense(imageUri: Uri) {
        authUiState.update {
            it.copy(licenseUri = imageUri)
        }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val role: Role? = null,
    val licenseUri: Uri? = null
)
