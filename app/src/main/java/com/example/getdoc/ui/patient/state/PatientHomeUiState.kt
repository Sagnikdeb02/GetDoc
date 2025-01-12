package com.example.getdoc.ui.patient.state

import com.example.getdoc.data.model.DoctorInfo

data class PatientHomeUiState(
    val name: String = "",
    val location: String = "",
    val searchQuery: String = "",
    val doctors: List<DoctorInfo> = emptyList()
)

