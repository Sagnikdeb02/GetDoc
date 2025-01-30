package com.example.getdoc.data.model

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
    val userId: String = "", // ✅ This should be the Firebase UID of the doctor
    val about: String = "",
    val consultingFee: Int = 0,
    val degree: String = "",
    val dob: String = "",
    val experience: Int = 0,
    val licenseUrl: String = "",
    val status: String = "",
    val location: String = "",
    val name: String = "",
    val profileImage: String = "",
    val rating: Double = 0.0,
    val specialization: String = "",
    val isApproved: Boolean = false
)