package com.example.getdoc.ui.patient.state

import com.example.getdoc.ui.patient.appointment.Appointment

data class PatientAppointmentState(
    val activeAppointments: List<Appointment> = emptyList(),
    val previousAppointments: List<Appointment> = emptyList()
)
