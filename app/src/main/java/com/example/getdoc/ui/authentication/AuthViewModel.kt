package com.example.getdoc.ui.authentication

import android.util.Log
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Uninitialized)
    val authState = _authState.asStateFlow()
    var verificationCheckJob: Job? = null

    init {
        loadUserData()
    }


    fun signInUser(email: String, password: String) {
        _authState.value = AuthState.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = it.user!!
                val userEmail = user.email
                if (userEmail != null) {
                    db.collection("users").document(userEmail).get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                val roleString = document.getString("role")
                                val role = Role.valueOf(roleString ?: Role.PATIENT.name)
                                if (role == Role.DOCTOR) {
                                    db.collection("doctors").document(userEmail).get()
                                        .addOnSuccessListener { doctorDocument ->
                                            if (doctorDocument.exists()) {
                                                _authState.value = AuthState.Authenticated(user, role)
                                                Log.d(
                                                     "AuthViewModel",
                                                    "Doctor data: ${doctorDocument.data}"
                                                )
                                            }
                                        }
                                } else if (role == Role.PATIENT) {
                                    db.collection("patients").document(userEmail).get()
                                        .addOnSuccessListener { doctorDocument ->
                                            _authState.value = AuthState.Authenticated(user, role)
                                        }
                                }
                            }
                        }
                }

            }.addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "Unknown error")
            }
    }


    fun signUpUser(email: String, password: String, role: Role) {
        _authState.value = AuthState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                authResult.user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                    if (verificationTask.isSuccessful) {
                        checkEmailVerification()
                        _authState.value = AuthState.VerificationEmailSent
                    } else {
                        _authState.value = AuthState.Error("Failed to send verification email: ${verificationTask.exception?.message}")
                    }
                }

                val userMap = hashMapOf(
                    "email" to email,
                    "role" to role.name
                )

               db.collection("users")
                    .document(email)
                    .set(userMap)
                   .addOnSuccessListener {
                       _authState.value = AuthState.Authenticated(user = authResult.user!!, role)
                       signInUser(email, password)
                   }
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "Unknown error")
            }
    }
    // Periodically check if the email is verified
    fun checkEmailVerification() {
        verificationCheckJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val user = firebaseAuth.currentUser?.also {
                    it.reload()
                }
                if (user != null && user.isEmailVerified) {
                    loadUserData()
                    verificationCheckJob?.cancel()
                    break
                }
                delay(2000)
            }
        }
    }

    private fun loadUserData() {
        Log.d("AuthViewModel", "Loading user data")
        val user = firebaseAuth.currentUser
        if (user != null) {
            val userEmail = user.email
            if (userEmail != null) {
                Log.d("AuthViewModel", "User email: $userEmail")
                db.collection("users").document(userEmail).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val roleString = document.getString("role")
                            val role = Role.valueOf(roleString ?: Role.PATIENT.name)
                            _authState.value = AuthState.Authenticated(user, role)
                        } else {
                            _authState.value = AuthState.Error("User data not found")
                        }
                    }
            }
        } else {
            _authState.value = AuthState.Error("User not authenticated")
        }
    }


    fun signOutUser() {
        firebaseAuth.signOut()
        _authState.value = AuthState.Uninitialized
    }

}

sealed class AuthState {
    object Uninitialized : AuthState()
    object Loading : AuthState()
    object VerificationEmailSent : AuthState()
    data class Authenticated(val user: FirebaseUser, val role: Role) : AuthState()
    data class Error(val message: String) : AuthState()
}

enum class Role {
    ADMIN, DOCTOR, PATIENT
}