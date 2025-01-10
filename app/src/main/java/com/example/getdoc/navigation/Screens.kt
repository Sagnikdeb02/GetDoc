package com.example.getdoc.navigation

import kotlinx.serialization.Serializable

@Serializable data object SplashScreen
@Serializable data object LoginScreen
@Serializable data object ChooseRoleScreen
@Serializable data object SignUpScreen
@Serializable data object UploadLicenseScreen
@Serializable data object LicenseConfirmationScreen

@Serializable data object PatientHomeScreen
@Serializable data object DoctorHomeScreen



@Serializable data object PatientAppointmentScreen
@Serializable data object SearchDoctorScreen
@Serializable data object DoctorDetailsScreen
@Serializable data object PatientBookingScreen
@Serializable data object PatientProfileScreen
@Serializable data object AppointmentConfirmationScreen

@Serializable data object PatientDetailsScreen
@Serializable data object DoctorProfileScreen
@Serializable data object DoctorCredentialsScreen

enum class Role {
    PATIENT,
    DOCTOR
}