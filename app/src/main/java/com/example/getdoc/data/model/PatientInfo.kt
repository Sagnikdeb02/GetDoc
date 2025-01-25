package com.example.getdoc.data.model

data class PatientUiState(
    val firstName: String = "",
    val lastName: String = "",
    val age: String = "",
    val gender: String = "",
    val username: String = "",
    val relation: String = "Self",
    val dob: String = "",
    val profileImageUrl: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
