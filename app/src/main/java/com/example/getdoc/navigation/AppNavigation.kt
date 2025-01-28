package com.example.getdoc.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.ui.authentication.AuthenticationViewModel
import com.example.getdoc.ui.authentication.ChooseRoleScreen
import com.example.getdoc.ui.authentication.LogInScreen
import com.example.getdoc.ui.authentication.SignupScreen
import com.example.getdoc.ui.authentication.SplashScreen
import com.example.getdoc.ui.authentication.doctor_registration.AdminHomeScreen
import com.example.getdoc.ui.authentication.doctor_registration.DoctorRegistrationScreen
import com.example.getdoc.ui.doctor.DoctorHomeScreen
import com.example.getdoc.ui.doctor.DoctorViewModel
import com.example.getdoc.ui.doctor.profile.MyCredentialsPageComponent
import com.example.getdoc.ui.patient.PatientProfileInputScreen
import com.example.getdoc.ui.doctor.profile.DoctorProfileScreen
import com.example.getdoc.ui.doctor.profile.UploadDoctorProfilePictureScreen
import com.example.getdoc.ui.patient.AllDoctorsScreen
import com.example.getdoc.ui.patient.PatientHomeScreen
import com.example.getdoc.ui.patient.PatientViewModel
import com.example.getdoc.ui.patient.appointment.AddPatientScreen
import com.example.getdoc.ui.patient.appointment.DoctorDetailsScreen
import com.example.getdoc.ui.patient.state.PatientHomeUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client

@Composable
fun AppNavigation(modifier: Modifier = Modifier, client: Client, firestore: FirebaseFirestore) {
    val navController = rememberNavController()
    val authViewModel: AuthenticationViewModel = viewModel()
    val authUiState by authViewModel.authUiState.collectAsStateWithLifecycle()
    val firebaseUser by authViewModel.firebaseUser.collectAsStateWithLifecycle()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = LoginScreen) {
        composable<SplashScreen> {
            SplashScreen(
                onSplashComplete = {
                    if (firebaseUser != null) {
                        navController.navigate(PatientHomeScreen) {
                            popUpTo(SplashScreen) { inclusive = true }
                        }
                    } else {
                        navController.navigate(LoginScreen) {
                            popUpTo(SplashScreen) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<LoginScreen> {
            LogInScreen(
                auth = FirebaseAuth.getInstance(),
                navController = navController,
                onLoginSuccess = {
                    navController.navigate(PatientHomeScreen) {
                        popUpTo(LoginScreen) { inclusive = true }
                    }
                },
                firestore = firestore
            )
        }
        composable<ChooseRoleScreen> {
            ChooseRoleScreen(
                navController = navController,
                onDoctorClick = { navController.navigate(DoctorRegistrationScreen) },
                onPatientClick = { navController.navigate(SignUpScreen) }
            )
        }

        composable<AdminHomeScreen> {
            AdminHomeScreen(
                client = client,
                bucketId = "678dd5d30039f0a22428",
                navController = navController,
                viewModel = AuthenticationViewModel(),
                firestore = firestore,
            )
        }


        composable<SignUpScreen> {
            SignupScreen(
                auth = FirebaseAuth.getInstance(),
                navController = navController,
                onSignUpSuccess = {
                    navController.navigate(LoginScreen) {
                        popUpTo(SignUpScreen) { inclusive = true }
                    }
                }
            )
        }





        composable<DoctorHomeScreen> {
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

        composable("DoctorDetailsScreen/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            DoctorDetailsScreen(
                doctorId = doctorId,
                onBackClick = { navController.popBackStack() },
                client = client,
                viewModel = PatientViewModel(client, firestore)
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

        composable<PatientProfileInputScreen> {
            PatientProfileInputScreen(
                viewModel = PatientViewModel(client, firestore)
            )
        }

//        composable<DoctorProfileInputScreen> {
//            DoctorProfileInputScreen(
//                viewModel = DoctorViewModel(client, firestore)
//            )
//        }

        composable<AddPatientScreen> {
            AddPatientScreen(
                viewModel = PatientViewModel(client, firestore),
                onBackClick = {}
            )
        }

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
        composable<DoctorRegistrationScreen> {
            DoctorRegistrationScreen (
                client,firestore
            )
        }

        composable<DoctorProfileScreen> {
            DoctorProfileScreen(
                onLogoutClick = {
                    navController.navigate(LoginScreen) {
                        popUpTo(DoctorProfileScreen) { inclusive = true }
                    }
                },
                modifier = modifier,
                onOptionClick = {},
                onHomeClick = { navController.navigate(DoctorHomeScreen) },
                onAppointmentsClick = { navController.navigate(AppointmentConfirmationScreen) },
                onProfileClick = { navController.navigate(DoctorProfileScreen) },
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







                }
}
