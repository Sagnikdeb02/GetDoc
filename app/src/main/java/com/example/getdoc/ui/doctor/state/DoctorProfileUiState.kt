package com.example.getdoc.ui.doctor.state

import android.net.Uri

data class DoctorProfileUiState(
    val usernameInput: String = "",
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
