package com.example.getdoc.ui.doctor.state

data class DoctorCredentialScreenUiState(
    val id: String? = null,
    val name: String? = null,
    val degree: String? = null,
    val speciality: String? = null,
    val dob: String? = null,
    val address: String? = null,
    val fee: String? = null,
    val aboutYou: String? = null,
    val licenseUrl: String? = null,
    val status: String = "pending",  // 🔹 Set default value instead of null
    val rejectionReason: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

