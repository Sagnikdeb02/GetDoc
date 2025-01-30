package com.example.getdoc.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.getdoc.ui.doctor.profile.DoctorBottomNavigationBar
import com.example.getdoc.ui.patient.AllDoctorsScreen
import com.example.getdoc.ui.patient.PatientHomeScreen
import com.example.getdoc.ui.patient.PatientProfileInputScreen
import com.example.getdoc.ui.patient.PatientProfileScreen
import com.example.getdoc.ui.patient.PatientViewModel
import com.example.getdoc.ui.patient.appointment.ActivesAppointmentScreen
import com.example.getdoc.ui.patient.appointment.AddPatientScreen
import com.example.getdoc.ui.patient.appointment.DoctorDetailsScreen
import com.example.getdoc.ui.patient.component.PatientBottomNavigationBar
import com.example.getdoc.ui.patient.state.PatientHomeUiState
import com.example.getdoc.ui.patient.state.PatientProfileUiState
import com.example.getdoc.ui.theme.ui.patient.appointments.BookingDoctorScreen
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import kotlinx.serialization.Serializable


@Serializable
data object PatientNavigation


@Composable
fun PatientNavigation(
    client: Client,
    firestore: FirebaseFirestore,
    modifier: Modifier = Modifier,
) {

    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            PatientBottomNavigationBar(
                onHomeClick = { navController.navigate(PatientHomeScreen) },
                onAppointmentsClick = { navController.navigate(PatientAppointmentScreen) },
                onDoctorsClick = { navController.navigate(AllDoctorsScreen) },
                onProfileClick = { navController.navigate(PatientProfileScreen) }
            )

        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = PatientHomeScreen,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable<PatientHomeScreen> {
                val patientHomeUiState = PatientHomeUiState(
                    name = "",
                    location = "",
                    searchQuery = "",
                    doctors = listOf()  // Provide an empty list or actual doctor data
                )

                PatientHomeScreen(
                    patientHomeUiState,
                    onHomeClick = {
                        navController.navigate(PatientHomeScreen)
                    },
                    onAppointmentsClick = {
                        navController.navigate(AppointmentConfirmationScreen)
                    },
                    onDoctorsClick = {
                        navController.navigate(SearchDoctorScreen)
                    },
                    onProfileClick = {
                        navController.navigate(PatientProfileScreen)
                    },
                    onSearch = { query ->
                        // Handle search logic here
                    },
                    viewModel = PatientViewModel(client, firestore),
                    firestore = firestore,
                    client = client,
                    navController = navController
                )
            }

            composable<AllDoctorsScreen> {
                AllDoctorsScreen(
                    onHomeClick = {},
                    onAppointmentsClick = {},
                    onDoctorsClick = {},
                    onProfileClick = {},
                    onSearch = {},
                    firestore = firestore,
                    client = client,
                    navController = navController,
                    // state = TODO()
                )
            }

            composable<AddPatientScreen> {
                AddPatientScreen(
                    viewModel = PatientViewModel(client, firestore),
                    onBackClick = {}
                )
            }
            composable("doctor_details/{doctorId}") { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                if (doctorId.isNotEmpty()) {
                    DoctorDetailsScreen(
                        navController,
                        doctorId = doctorId,
                        onBackClick = { navController.popBackStack() },
                        viewModel = PatientViewModel(client, firestore)
                    )
                } else {
                    Log.e("Navigation", "Error: Doctor ID is empty!")
                }
            }

            composable("booking_screen/{doctorId}") { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                BookingDoctorScreen(
                    doctorId = doctorId,
                    onBackClick = { navController.popBackStack() },
                    selectedDate = "",
                    onDateSelected = {},
                    onProceedClick = {},
                    firestore = firestore                )
            }




            composable<PatientProfileInputScreen> {
                PatientProfileInputScreen(
                    viewModel = PatientViewModel(client, firestore)
                )
            }
            composable<PatientProfileScreen> {
                PatientProfileScreen(
                    onLogoutClick = {},
                    onEditClick = {navController.navigate(PatientProfileInputScreen)},
                    onOptionClick = {},
                    patientViewModel = PatientViewModel(client, firestore)
                )
            }
            composable<PatientAppointmentScreen> {

            }
            composable<PatientProfileInputScreen> {
                PatientProfileInputScreen(
                    viewModel = PatientViewModel(client, firestore)
                )
            }
        }
    }
}



