package com.example.getdoc.ui.authentication.doctor_registration

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

class DoctorRegistrationViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun registerDoctor(
        username: String,
        email: String,
        password: String,
        licenseUri: Uri,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Create user in Firebase Auth
                val user = auth.createUserWithEmailAndPassword(email, password).await().user
                val userId = user?.uid ?: throw Exception("User ID is null")

                // Upload license image to Firebase Storage
                val licenseRef = storage.reference.child("doctor_licenses/$userId.jpg")
                licenseRef.putFile(licenseUri).await()

                // Get the download URL for the uploaded license
                val licenseUrl = licenseRef.downloadUrl.await().toString()

                // Save doctor details in Firestore
                val doctorData = mapOf(
                    "username" to username,
                    "email" to email,
                    "licenseUrl" to licenseUrl,
                    "isApproved" to false
                )
                db.collection("doctors").document(userId).set(doctorData).await()

                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Registration failed")
            }
        }
    }
}
