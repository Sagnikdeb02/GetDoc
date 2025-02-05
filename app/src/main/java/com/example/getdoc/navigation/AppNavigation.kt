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
import com.example.getdoc.ForgetPasswordScreen
import com.example.getdoc.ui.authentication.AdminHomeScreen
import com.example.getdoc.ui.authentication.AuthState
import com.example.getdoc.ui.authentication.AuthViewModel
import com.example.getdoc.ui.authentication.AuthenticationViewModel
import com.example.getdoc.ui.authentication.ChooseRoleScreen
import com.example.getdoc.ui.authentication.LogInScreen
import com.example.getdoc.ui.authentication.RegistrationScreen
import com.example.getdoc.ui.authentication.Role
import com.example.getdoc.ui.authentication.SplashScreen
import com.example.getdoc.ui.authentication.WaitingForVerificationScreen
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client

@Composable
fun AppNavigation(modifier: Modifier = Modifier, client: Client, firestore: FirebaseFirestore) {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = SplashScreen) {
        composable<SplashScreen> {

            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Authenticated -> {
                        val userRole = (authState as AuthState.Authenticated).role
                        when (userRole) {
                            Role.ADMIN -> navController.navigate(AdminHomeScreen) {
                                popUpTo(0) { inclusive = true }
                            }

                            Role.DOCTOR -> navController.navigate(DoctorHomeScreen) {
                                popUpTo(0) { inclusive = true }
                            }

                            Role.PATIENT -> navController.navigate(PatientHomeScreen) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }

                    is AuthState.Error, AuthState.Uninitialized -> {
                        navController.navigate(LoginScreen) { popUpTo(0) { inclusive = true } }
                    }

                    AuthState.Loading -> { /* Stay on splash screen */ }
                    AuthState.VerificationEmailSent -> navController.navigate(WaitingForVerificationScreen)
                    AuthState.PendingApproval, is AuthState.Rejected -> navController.navigate(LoginScreen)
                }
            }
            SplashScreen()
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
                    when (it) {
                        Role.ADMIN -> navController.navigate(AdminHomeScreen)
                        Role.DOCTOR -> navController.navigate(DoctorHomeScreen)
                        Role.PATIENT -> navController.navigate(PatientHomeScreen)
                    }

                },
                onSignUpClick = { navController.navigate(ChooseRoleScreen) },
                onVerificationEmailSent = { navController.navigate(WaitingForVerificationScreen) },
                onForgotPasswordClick = {
                    navController.navigate(ForgetPasswordScreen)
                }
            )
        }

        composable<ForgetPasswordScreen> {
            ForgetPasswordScreen(
                viewModel = authViewModel,
                onPasswordResetSuccess = {
                    navController.navigateUp()
                }
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
                viewModel = authViewModel,
                client = client,
                bucketId = "678dd5d30039f0a22428",
                firestore = firestore,
            ) {
                navController.navigate(LoginScreen) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }



        composable<DoctorHomeScreen> {
            DoctorNavigation(
                client = client,
                firestore = firestore,
                authViewModel = authViewModel,
                onLogoutClick = {
                    navController.navigate(LoginScreen) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }


        composable<PatientHomeScreen> {
            PatientNavigation(
                client = client,
                firestore = firestore,
                authViewModel = authViewModel,
                onLogoutClick = {
                    navController.navigate(LoginScreen) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }



    }
}