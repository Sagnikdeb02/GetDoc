package com.example.getdoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.getdoc.navigation.AppNavigation
import com.example.getdoc.ui.doctor.DoctorViewModel
import com.example.getdoc.ui.doctor.profile.MyCredentialsPageComponent
import com.example.getdoc.ui.patient.PatientProfileInputScreen
import com.example.getdoc.ui.patient.PatientTopBar
import com.example.getdoc.ui.patient.PatientViewModel
import com.example.getdoc.ui.patient.TopDoctorsSection
import com.example.getdoc.ui.patient.appointment.AddPatientScreen

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)  // Initialize Firebase
        FirebaseFirestore.setLoggingEnabled(true)
        val firestore = FirebaseFirestore.getInstance()
        val client = Client(this).setProject("678dd5bc0024c854ef06")
        setContent {


           AppNavigation(Modifier ,client,firestore)

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


//            AddPatientScreen(
//                viewModel= PatientViewModel(client,firestore),
//                onBackClick = {}
//            )


//            PatientProfileInputScreen(
//                viewModel= PatientViewModel(client,firestore),
//          )

//           DoctorProfileInputScreen(
//               viewModel= DoctorViewModel(client,firestore),
//           )

  //        TopDoctorsSection(PatientViewModel(client,firestore),client)
        }
    }
}




