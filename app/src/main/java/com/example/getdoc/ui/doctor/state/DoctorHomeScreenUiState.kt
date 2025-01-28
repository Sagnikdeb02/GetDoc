package com.example.getdoc.ui.doctor.state

import com.example.getdoc.data.model.DoctorAppointment

data class DoctorHomeScreenUiState(
    val profileImageUrl: String = "",
    val doctorUsername: String = "",
    val location: String = "",
    val searchQuery: String = "",
    val pendingAppointments: List<DoctorAppointment> = emptyList(),
)