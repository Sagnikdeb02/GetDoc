package com.example.getdoc.ui.patient.state

import android.net.Uri

data class PatientUiState(
    val firstName: String = "",
    val lastName: String = "",
    val age: String = "",
    val gender: String = "",
    val relation: String = "Self",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)