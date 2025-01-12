package com.example.getdoc.ui.patient.state

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow

class PatientViewModel: ViewModel() {
    val firestore = FirebaseFirestore.getInstance()

    var homeUiState = MutableStateFlow(PatientHomeUiState())
        private set

    var appointmentUiState = MutableStateFlow(PatientAppointmentState())
        private set

    init {
        getFirebaseData()
    }

    private fun getFirebaseData() {
        // TODO: Get data from Firebase and update the UI states
    }

    fun onCancelAppointmentClick() {

    }

}