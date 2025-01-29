package com.example.getdoc.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.getdoc.ui.doctor.DoctorHomeScreen
import com.example.getdoc.ui.doctor.DoctorViewModel
import com.example.getdoc.ui.doctor.profile.BottomBarComponent
import com.example.getdoc.ui.doctor.profile.DoctorBottomNavigationBar
import com.example.getdoc.ui.doctor.profile.DoctorProfileOption
import com.example.getdoc.ui.doctor.profile.DoctorProfileScreen
import com.example.getdoc.ui.doctor.profile.MyCredentialsPageComponent
import com.example.getdoc.ui.doctor.profile.UploadDoctorProfilePictureScreen
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import kotlinx.serialization.Serializable

@Serializable
data object DoctorNavigation


@Composable
fun DoctorNavigation(
    client: Client,
    firestore: FirebaseFirestore,
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            DoctorBottomNavigationBar(
                onHomeClick = { navController.navigate(DoctorHomeScreen) },
                onAppointmentsClick = { navController.navigate(DoctorCredentialsScreen) },
                onProfileClick = { navController.navigate(DoctorProfileScreen) }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DoctorProfileScreen,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<DoctorHomeScreen> {

                // TODO: add patient lists in the home screen
                DoctorHomeScreen(
                    viewModel = DoctorViewModel(client,firestore),
                    onHomeClick = {

                    },
                    onAppointmentsClick = {
                        navController.navigate(DoctorCredentialsScreen)//(AppointmentConfirmationScreen)
                    },
                    onProfileClick = {
                        navController.navigate(DoctorProfileScreen)
                    },
                    navController = navController,
                )
            }

            composable<DoctorCredentialsScreen> {
                MyCredentialsPageComponent(
                    viewModel = DoctorViewModel(client, firestore),
                    onHomeClick = {
                        // Navigate to home screen
                        navController.navigate(DoctorHomeScreen) {
                            popUpTo(DoctorCredentialsScreen) { inclusive = true }
                        }
                    },
                    onAppointmentsClick = {
                        // Navigate to appointments screen
                        navController.navigate(PatientAppointmentScreen)
                    },
                    onProfileClick = { navController.navigate(DoctorProfileScreen) },
                    modifier = Modifier.fillMaxSize()
                )
            }


            composable(
                route = "UploadDoctorProfilePictureScreen/{doctorId}",
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                UploadDoctorProfilePictureScreen(
                    viewModel = DoctorViewModel(client,firestore), // Pass your DoctorViewModel instance
                    doctorId = doctorId
                )
            }


            composable<DoctorProfileScreen> {
                DoctorProfileScreen(
                    onLogoutClick = {

                    },
                    onOptionClick = { option ->
                        when (option) {
                            DoctorProfileOption.MY_CREDENTIALS -> {
                                navController.navigate(DoctorCredentialsScreen)
                            }
                            DoctorProfileOption.CHANGE_CONTACT -> {

                            }
                            DoctorProfileOption.CHANGE_PASSWORD -> {

                            }
                            DoctorProfileOption.ABOUT_US -> {

                            }
                            DoctorProfileOption.HELP -> {

                            }
                        }

                    }
                )
            }
        }
    }

}

