package com.example.getdoc.ui.patient.state


import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue

data class PatientProfileUiState(
    val usernameInput: String = "",
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)