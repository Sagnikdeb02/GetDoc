package com.example.getdoc.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.getdoc.ui.patient.AllDoctorsScreen
import com.example.getdoc.ui.patient.PatientHomeScreen
import com.example.getdoc.ui.patient.PatientProfileInputScreen
import com.example.getdoc.ui.patient.PatientViewModel
import com.example.getdoc.ui.patient.appointment.AddPatientScreen
import com.example.getdoc.ui.patient.appointment.DoctorDetailsScreen
import com.example.getdoc.ui.patient.state.PatientHomeUiState
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import kotlinx.serialization.Serializable


@Serializable
data object PatientNavigation

fun NavGraphBuilder.patientNavigation(
    client: Client,
    firestore: FirebaseFirestore,
    navController: NavHostController
) {

    navigation<PatientNavigation>(
        startDestination = PatientHomeScreen
    ) {

        composable<PatientHomeScreen> {
            val patientHomeUiState = PatientHomeUiState(
                name = "John Doe",
                location = "New York",
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
                // state = TODO()
            )
        }

        composable<AddPatientScreen> {
            AddPatientScreen(
                viewModel = PatientViewModel(client, firestore),
                onBackClick = {}
            )
        }

        composable("DoctorDetailsScreen/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            DoctorDetailsScreen(
                doctorId = doctorId,
                onBackClick = { navController.popBackStack() },
                client = client,
                viewModel = PatientViewModel(client, firestore)
            )
        }


        composable<PatientProfileInputScreen> {
            PatientProfileInputScreen(
                viewModel = PatientViewModel(client, firestore)
            )
        }

    }

}