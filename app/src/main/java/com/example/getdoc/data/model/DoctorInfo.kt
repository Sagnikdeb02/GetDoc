package com.example.getdoc.data.model

import com.example.getdoc.R

/**
 * Represents a doctor's information.
 * @property name The doctor's name.
 * @property degree The doctor's degree.
 * @property specialization The doctor's specialization. e.g. Cardiologist, Dentist
 * @property experience The doctor's years of experience. e.g. 1, 2, (in years)
 * @property location The doctor's location. e.g. Delhi, Mumbai, etc.
 * @property consultingFee The doctor's consulting fee. e.g. 1000, 2000, etc.
 */
data class DoctorInfo(
    val about: String = "",
    val consultingFee: Int = 0,
    val degree: String = "",
    val dob: String = "",
    val experience: Int = 0,
    val location: String = "",
    val name: String = "",
    val profileImage: String = "",  // Ensure this is treated as a string
    val rating: Double = 0.0,
    val specialization: String = ""
)
