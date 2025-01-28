package com.example.getdoc.navigation

import com.example.getdoc.ui.authentication.Role
import kotlinx.serialization.Serializable

@Serializable data object SplashScreen
@Serializable data object LoginScreen
@Serializable data object ChooseRoleScreen
@Serializable data class RegistrationScreen(val role: Role)
@Serializable data object WaitingForVerificationScreen

@Serializable data object PatientHomeScreen
@Serializable data object DoctorHomeScreen
@Serializable data object AdminHomeScreen



@Serializable data object PatientAppointmentScreen
@Serializable data object SearchDoctorScreen
@Serializable data object AllDoctorsScreen
@Serializable data object AboutDoctorScreen
@Serializable data object DoctorDetailsScreen
@Serializable data object PatientBookAppointmentScreen
@Serializable data object AddPatientScreen
@Serializable data object PatientPaymentScreen
@Serializable data object PatientReviewScreen
@Serializable data object PatientProfileScreen
@Serializable data object PatientProfileInputScreen
@Serializable data object AppointmentConfirmationScreen


@Serializable data object PatientDetailsScreen
@Serializable data object DoctorProfileScreen
@Serializable data object DoctorProfileInputScreen
@Serializable data object DoctorCredentialsScreen
@Serializable data object DoctorAvailabilityScreen
