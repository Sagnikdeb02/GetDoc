package com.example.getdoc.ui.doctor.state

data class DoctorCredentialScreenUiState(
    val name: String? = null,
    val degree: String? = null,
    val speciality: String? = null,
    val dob: String? = null,
    val address: String? = null,
    val fee: String? = null,
    val aboutYou: String? = null,
    val licenseUrl: String? = null,
    val status: String? = null,  // ✅ Change to nullable to avoid wrong default
    val rejectionReason: String? = null, // Keep this as it is
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
