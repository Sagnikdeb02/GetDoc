package com.example.getdoc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.getdoc.ui.authentication.AuthenticationViewModel
import com.example.getdoc.ui.authentication.ChooseRoleScreen
import com.example.getdoc.ui.authentication.doctor_registration.LicenseConfirmationScreen
import com.example.getdoc.ui.authentication.LogInScreen
import com.example.getdoc.ui.authentication.SignupScreen
import com.example.getdoc.ui.authentication.SplashScreen
import com.example.getdoc.ui.authentication.doctor_registration.UploadLicenseScreen
import com.example.getdoc.ui.authentication.doctor_registration.UploadLicenseViewModel
import com.example.getdoc.ui.doctor.DoctorHomeScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthenticationViewModel = viewModel()
    val authUiState by authViewModel.authUiState.collectAsStateWithLifecycle()
    val firebaseUser by authViewModel.firebaseUser.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = SplashScreen) {
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
                viewModel = authViewModel,
                onSignUpClick = {
                    navController.navigate(SignUpScreen)
                },
//                onForgotPasswordClick = {
////                    navController.navigate(ForgotPasswordScreen)
//                },
                onLoginSuccess = {
                    navController.navigate(PatientHomeScreen)
                }
            )
        }

        composable<SignUpScreen> {
            SignupScreen(
                viewModel = authViewModel,
                onSignUpSuccess = {
                    navController.navigate(ChooseRoleScreen)
                }
            )
        }

        composable<LicenseConfirmationScreen> {
            LicenseConfirmationScreen()
        }

        composable<ChooseRoleScreen> {
            ChooseRoleScreen(
                onDoctorClick = {
                    navController.navigate(UploadLicenseScreen)
                },
                onPatientClick = {
                    navController.navigate(PatientHomeScreen)
                }
            )
        }


        composable<PatientHomeScreen> {
            DoctorHomeScreen()
        }
        composable<DoctorHomeScreen> {

        }

        composable<UploadLicenseScreen> {
            val uploadLicenseViewModel: UploadLicenseViewModel = viewModel()
            UploadLicenseScreen(
                viewModel = uploadLicenseViewModel,
            )
        }



    }
}