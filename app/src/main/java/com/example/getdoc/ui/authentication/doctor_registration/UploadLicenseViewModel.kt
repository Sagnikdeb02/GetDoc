package com.example.getdoc.ui.authentication.doctor_registration

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UploadLicenseViewModel : ViewModel() {
    private val _isUploadComplete = MutableStateFlow(false)
    val isUploadComplete: StateFlow<Boolean> = _isUploadComplete

    fun uploadLicense(imageUri: Uri) {
        viewModelScope.launch {
            // Simulate license upload process
            delay(2000) // Replace this with your actual upload logic
            _isUploadComplete.value = true
        }
    }
}