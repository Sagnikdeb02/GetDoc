package com.example.getdoc.ui.patient.state

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue

data class PatientProfileUiState(
    val usernameInput: TextFieldValue = TextFieldValue(),
    val imageUri: Uri? = null,


    val username: String = usernameInput.text,
)