package com.example.getdoc.ui.theme.ui.patient.Appointments.BookingScreenElements

import com.example.getdoc.R

data class DoctorInfo(
    val name: String,
    val degree: String = "",
    val specialization: String,
    val experience: Int = 0,
    val location: String,
    val consultingFee: Int,
    val rating: Float = 0f,
    val profileImage: Int = R.drawable.doctor_profile, // Default image resource
    val about: String = ""

)