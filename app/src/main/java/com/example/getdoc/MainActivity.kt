package com.example.getdoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.getdoc.navigation.AppNavigation
import com.example.getdoc.ui.doctor.DoctorHomeScreen

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)  // Initialize Firebase

        setContent {
             //AppNavigation()
            DoctorHomeScreen(
                onHomeClick = { /* Handle Home Click */ },
                onAppointmentsClick = { /* Handle Appointments Click */ },
                onProfileClick = { /* Handle Profile Click */ }
            )

        }
    }
}




