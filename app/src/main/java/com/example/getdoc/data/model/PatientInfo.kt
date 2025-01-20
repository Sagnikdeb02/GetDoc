package com.example.getdoc.data.model

data class PatientInfo(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val age: Int = 0,
    val gender: String = "",
    val relation: String = "Self",
    val dob: String = ""
)
// Data class for patient