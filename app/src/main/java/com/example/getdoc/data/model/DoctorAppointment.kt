package com.example.getdoc.data.model

data class DoctorAppointment(
    val doctor: DoctorInfo,
    val patient: PatientInfo,
    val date: String,
    val time: String,
    val status: AppointmentStatus
)

enum class AppointmentStatus {
    PENDING, ACCEPTED, REJECTED
}