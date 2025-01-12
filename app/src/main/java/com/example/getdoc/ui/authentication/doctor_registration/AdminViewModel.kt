package com.example.getdoc.ui.authentication.doctor_registration

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    val unapprovedDoctors = flow {
        val snapshot = db.collection("doctors").whereEqualTo("isApproved", false).get().await()
        val doctors = snapshot.documents.map { doc ->
            Doctor(
                id = doc.id,
                username = doc.getString("username") ?: "",
                email = doc.getString("email") ?: ""
            )
        }
        emit(doctors)
    }

    fun approveDoctor(doctorId: String) {
        db.collection("doctors").document(doctorId).update("isApproved", true)
    }

    fun rejectDoctor(doctorId: String) {
        db.collection("doctors").document(doctorId).delete()
    }
}

data class Doctor(val id: String, val username: String, val email: String)
