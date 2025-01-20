package com.example.getdoc.ui.doctor.state
data class DoctorUiState(
    val name: String = "",
    val degree: String = "",
    val speciality: String = "",
    val dob: String = "",
    val address: String = "",
    val fee: String = "",
    val aboutYou: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

