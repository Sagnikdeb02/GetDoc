package com.example.getdoc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.example.getdoc.ui.authentication.ChooseRoleScreen
import com.example.getdoc.ui.authentication.LogIn
import com.example.getdoc.ui.authentication.SplashScreen
import com.example.getdoc.ui.doctor.DoctorHomeScreen

@Composable
fun AppNavigation(auth: FirebaseAuth, modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SplashScreen) {
        composable<SplashScreen> {
            LaunchedEffect(Unit) {
                if (auth.currentUser != null) {
                    navController.navigate(PatientHomeScreen)
                } else {
                    navController.navigate(LoginScreen)
                }
            }
            SplashScreen()
        }

        composable<LoginScreen> {
            LogIn(
                auth,
                navController
            ) {

            }
        }

        composable<ChooseRoleScreen> {
            ChooseRoleScreen()
        }

        composable<PatientSignUpScreen> {

        }

        composable<DoctorSignUpScreen> {

        }

        composable<PatientHomeScreen> {
            DoctorHomeScreen()
        }
        composable<DoctorHomeScreen> {

        }
    }
}