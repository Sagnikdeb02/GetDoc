package com.example.getdoc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.getdoc.ui.authentication.AuthState
import com.example.getdoc.ui.authentication.AuthViewModel
import com.example.getdoc.ui.authentication.AuthenticationViewModel
import com.example.getdoc.ui.authentication.ChooseRoleScreen
import com.example.getdoc.ui.authentication.LogInScreen
import com.example.getdoc.ui.authentication.RegistrationScreen
import com.example.getdoc.ui.authentication.Role
import com.example.getdoc.ui.authentication.SplashScreen
import com.example.getdoc.ui.authentication.WaitingForVerificationScreen
import com.example.getdoc.ui.authentication.doctor_registration.AdminHomeScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client

@Composable
fun AppNavigation(modifier: Modifier = Modifier, client: Client, firestore: FirebaseFirestore) {
    val navController = rememberNavController()
    val authenticationViewModel: AuthenticationViewModel = viewModel()
    val firebaseUser by authenticationViewModel.firebaseUser.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()



    NavHost(navController = navController, startDestination = SplashScreen) {
        composable<SplashScreen> {
            SplashScreen(
                onSplashComplete = {
                    when (authState) {
                        is AuthState.Authenticated -> {
                            when((authState as AuthState.Authenticated).role) {
                                Role.ADMIN -> {
                                    navController.navigate(AdminHomeScreen)
                                }
                                Role.DOCTOR -> {
                                    navController.navigate(DoctorHomeScreen)
                                }
                                Role.PATIENT -> {
                                    navController.navigate(PatientHomeScreen)
                                }
                            }
                        }
                        is AuthState.Error -> {
                            navController.navigate(LoginScreen)
                        }
                        AuthState.Loading -> {

                        }
                        AuthState.Uninitialized -> {

                        }
                        AuthState.VerificationEmailSent -> {
                            navController.navigate(WaitingForVerificationScreen)
                        }
                    }
                }
            )
        }
        composable<WaitingForVerificationScreen> {
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    when((authState as AuthState.Authenticated).role) {
                        Role.ADMIN -> {
                            navController.navigate(AdminHomeScreen) {
                                popUpTo(LoginScreen) { inclusive = true }
                            }
                        }
                        Role.DOCTOR -> {
                            navController.navigate(DoctorHomeScreen) {
                                popUpTo(LoginScreen) { inclusive = true }
                            }
                        }
                        Role.PATIENT -> {
                            navController.navigate(PatientHomeScreen) {
                                popUpTo(LoginScreen) { inclusive = true }
                            }
                        }
                    }
                }
            }
            WaitingForVerificationScreen()
        }

        composable<LoginScreen> {
            LogInScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(ChooseRoleScreen) {
                        popUpTo(LoginScreen) { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate(ChooseRoleScreen) }
            )
        }
        composable<ChooseRoleScreen> {
            ChooseRoleScreen(
                navController = navController,
                onDoctorClick = { navController.navigate(RegistrationScreen(Role.DOCTOR)) },
                onPatientClick = { navController.navigate(RegistrationScreen(Role.PATIENT)) }
            )
        }
        composable<RegistrationScreen> {
            val arguments = it.toRoute<RegistrationScreen>()
            RegistrationScreen(
                role = arguments.role,
                viewModel = authViewModel,
                onSignUpSuccess = {
                    navController.navigate(WaitingForVerificationScreen)
                }
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



        composable<DoctorHomeScreen> {
            DoctorNavigation(
                client = client,
                firestore = firestore
            )
        }

//        doctorNavigation(
//            client = client,
//            firestore = firestore,
//            navController = navController
//        )

        patientNavigation(
            client = client,
            firestore = firestore,
            navController = navController
        )
    }
}