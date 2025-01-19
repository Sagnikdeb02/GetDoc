package com.example.getdoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.getdoc.ui.doctor.profile.MyCredentialsPageComponent
import com.example.getdoc.ui.patient.appointment.AddPatientScreen

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)  // Initialize Firebase
        FirebaseFirestore.setLoggingEnabled(true)

        val firestore = FirebaseFirestore.getInstance()
        setContent {
          //   AppNavigation()
//            MyCredentialsPageComponent(
//                onHomeClick = {
//
//                },
//                onAppointmentsClick = {
//                },
//                onProfileClick = {
//
//                }
//            )
     AddPatientScreen (
         onBackClick = {}
     )

        }
    }
}




