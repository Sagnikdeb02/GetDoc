package com.example.getdoc


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.getdoc.ui.SignInAndStarting.LogIn
import com.example.getdoc.ui.patient.registration.PatientSignUp


import com.example.getdoc.ui.theme.ui.patient.DoctorListingPage
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)  // Initialize Firebase
        val auth = FirebaseAuth.getInstance()  // Get FirebaseAuth instance
        val db = FirebaseFirestore.getInstance()  // Get FirebaseFirestore instance

        setContent {
            AppNavigation(auth = auth)  // Pass auth and db to the composables
        }
    }
}

@Composable
fun AppNavigation(auth: FirebaseAuth) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LogIn(
                auth = auth,
                navController = navController,
                onLoginSuccess = {
                    navController.navigate("doctorListingPage") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("doctorListingPage") {
            DoctorListingPage()
        }
        composable("patientSignUp") {
            PatientSignUp(
                auth = auth,
                navController = navController,
                onSignUpSuccess = {
                    navController.navigate("doctorListingPage") {
                        popUpTo("patientSignUp") { inclusive = true }
                    }
                }
            )
        }
    }
}

