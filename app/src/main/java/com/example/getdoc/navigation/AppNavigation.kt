package com.example.getdoc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.getdoc.navigation.SignUpScreen
import com.example.getdoc.navigation.UploadLicenseScreen
import com.example.getdoc.ui.authentication.AuthenticationViewModel
import com.example.getdoc.ui.authentication.ChooseRoleScreen
import com.example.getdoc.ui.authentication.LicenseConfirmationScreen
import com.example.getdoc.ui.authentication.LogInScreen
import com.example.getdoc.ui.authentication.SignupScreen
import com.example.getdoc.ui.authentication.SplashScreen
import com.example.getdoc.ui.authentication.UploadLicenseScreen
import com.example.getdoc.ui.doctor.DoctorHomeScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthenticationViewModel = viewModel()
    val authUiState by authViewModel.authUiState.collectAsStateWithLifecycle()
    val firebaseUser by authViewModel.firebaseUser.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = SplashScreen) {
        composable<SplashScreen> {
            LaunchedEffect(firebaseUser) {
                if (firebaseUser != null) {
                    navController.navigate(PatientHomeScreen)
                } else {
                    navController.navigate(LoginScreen)
                }
            }
            SplashScreen()
        }

        composable<LoginScreen> {
            LogInScreen(
                viewModel = authViewModel,
                onSignUpClick = {
                    navController.navigate(SignUpScreen)
                },
                onForgotPasswordClick = {
//                    navController.navigate(ForgotPasswordScreen)
                },
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
        composable<UploadLicenseScreen> {
            UploadLicenseScreen(
                onImageUpload = {
                    navController.navigate(LicenseConfirmationScreen)
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
    }
}