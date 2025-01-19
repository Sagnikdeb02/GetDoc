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
    val name: String,
    val degree: String = "",
    val dob: String= " ",
    val specialization: String,
    val experience: Int = 0,
    val location: String,
    val consultingFee: Int,
    val rating: Float = 0f,
    val profileImage: Int = R.drawable.doctor_profile, // Default image resource
    val about: String = ""
)