package com.example.getdoc.ui.patient.state

data class PatientUiState(
    val firstName: String = "",
    val lastName: String = "",
    val age: String = "",
    val gender: String = "",
    val relation: String = "Self",
    val dob: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
