package com.example.getdoc.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.getdoc.ui.authentication.AuthState
import com.example.getdoc.ui.authentication.AuthViewModel
import com.example.getdoc.ui.doctor.DoctorHomeScreen
import com.example.getdoc.ui.doctor.DoctorViewModel
import com.example.getdoc.ui.doctor.profile.AppointmentScreen
import com.example.getdoc.ui.doctor.profile.BottomBarComponent
import com.example.getdoc.ui.doctor.profile.DoctorBottomNavigationBar
import com.example.getdoc.ui.doctor.profile.DoctorProfileOption
import com.example.getdoc.ui.doctor.profile.DoctorProfileScreen
import com.example.getdoc.ui.doctor.profile.MyCredentialsPageComponent
import com.example.getdoc.ui.doctor.profile.ProfileUpdateScreen
import com.example.getdoc.ui.patient.PatientViewModel
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import kotlinx.serialization.Serializable

@Serializable
data object DoctorNavigation


@Composable
fun DoctorNavigation(
    client: Client,
    firestore: FirebaseFirestore,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    LaunchedEffect(authState) {
        if (authState is AuthState.Uninitialized) {
            onLogoutClick()
        }
    }
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            DoctorBottomNavigationBar(
                onHomeClick = { navController.navigate(DoctorHomeScreen) },
                onAppointmentsClick = { navController.navigate(AppointmentsScreen) },
                onProfileClick = { navController.navigate(DoctorProfileScreen) }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DoctorHomeScreen,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<DoctorHomeScreen> {

                // TODO: add patient lists in the home screen
                DoctorHomeScreen(
                    firestore = firestore,
                    navController = navController,
                    client = client
                )
            }

            composable<DoctorCredentialsScreen> {
                MyCredentialsPageComponent(
                    viewModel = DoctorViewModel(client, firestore),
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable<DoctorProfileUpdateScreen> {
                ProfileUpdateScreen(
                    viewModel = DoctorViewModel(client, firestore),
                    client = client,
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable<DoctorProfileScreen> {
                val viewModel: AuthViewModel = viewModel()

                DoctorProfileScreen(
                    navController = navController,
                    firestore = firestore,
                    client = client,
                    onOptionClick = { option ->
                        when (option) {
                            DoctorProfileOption.MY_CREDENTIALS -> navController.navigate(DoctorCredentialsScreen)
                            DoctorProfileOption.CHANGE_CONTACT -> navController.navigate("change_contact")
                            DoctorProfileOption.CHANGE_PASSWORD -> navController.navigate("change_password")
                            DoctorProfileOption.ABOUT_US -> navController.navigate("about_us")
                            DoctorProfileOption.HELP -> navController.navigate("help")
                        }
                    },
                    onEditClick = {navController.navigate(DoctorProfileUpdateScreen)},
                    onLogoutClick = {
                        authViewModel.signOutUser()
                    }
                )
            }



            composable<AppointmentsScreen> {
                AppointmentScreen(
                    firestore = firestore,
                    navController = navController
                )

            }


        }
    }
}

