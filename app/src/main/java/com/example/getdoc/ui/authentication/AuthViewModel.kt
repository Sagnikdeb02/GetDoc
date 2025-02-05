package com.example.getdoc.ui.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.getdoc.navigation.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Uninitialized)
    val authState = _authState.asStateFlow()
    var verificationCheckJob: Job? = null

    // Default admin credentials
    private val defaultAdminEmail = "admin@getdoc.com"
    private val defaultAdminPassword = "admin123"

    init {
        if (verificationCheckJob == null) {
            loadUserData()
        }
    }

    fun signInUser(email: String, password: String) {
        _authState.value = AuthState.Loading

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                val userEmail = user?.email

                if (userEmail == null) {
                    _authState.value = AuthState.Error("Authentication failed: No email found")
                    return@addOnSuccessListener
                }

                db.collection("users").document(userEmail).get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            _authState.value = AuthState.Error("User data not found")
                            return@addOnSuccessListener
                        }
                        loadUserData()

                    }
                    .addOnFailureListener {
                        _authState.value = AuthState.Error("Error fetching user role: ${it.message}")
                    }
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "Unknown authentication error")
            }
    }



    fun signUpUser(email: String, password: String, role: Role) {
        _authState.value = AuthState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                authResult.user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                    if (!verificationTask.isSuccessful) {
                        _authState.value = AuthState.Error(
                            "Failed to send verification email: ${verificationTask.exception?.message}"
                        )
                        return@addOnCompleteListener
                    }
                    val userMap = hashMapOf(
                        "email" to email,
                        "role" to role.name
                    )
                    db.collection("users").document(email).set(userMap)
                        .addOnFailureListener {

                        }
                    _authState.value = AuthState.VerificationEmailSent
                    loadUserData()
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
                }
                delay(2000)
            }
        }
    }

    fun loadUserData() {
        _authState.value = AuthState.Loading
        firebaseAuth.currentUser?.reload()
        val user = firebaseAuth.currentUser
        if (user != null && user.isEmailVerified) {
            val userEmail = user.email
            if (userEmail != null) {
                db.collection("users").document(userEmail).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val roleString = document.getString("role")
                            val role = Role.valueOf(roleString ?: Role.PATIENT.name)
                            _authState.value = AuthState.Authenticated(user, role)
                        } else {
                            _authState.value = AuthState.Error("User data not found")
                        }
                    }.addOnFailureListener {
                        _authState.value = AuthState.Error("Error fetching user role: ${it.message}")
                    }
            }
        } else if (user != null && !user.isEmailVerified) {
            _authState.value = AuthState.VerificationEmailSent
            checkEmailVerification()
        } else {
            _authState.value = AuthState.Uninitialized
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
    object PendingApproval : AuthState() // New: Waiting for admin approval
    data class Authenticated(val user: FirebaseUser, val role: Role) : AuthState()
    data class Rejected(val reason: String) : AuthState() // New: If doctor is rejected
    data class Error(val message: String) : AuthState()
}


enum class Role {
    ADMIN, DOCTOR, PATIENT
}