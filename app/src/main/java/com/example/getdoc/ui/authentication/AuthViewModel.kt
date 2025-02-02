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

        loadUserData()
    }

    fun ensureAdminExists() {
        val user = firebaseAuth.currentUser
        if (user == null) {
            firebaseAuth.fetchSignInMethodsForEmail(defaultAdminEmail)
                .addOnSuccessListener { result ->
                    if (result.signInMethods.isNullOrEmpty()) {
                        // Admin does not exist, create it
                        firebaseAuth.createUserWithEmailAndPassword(defaultAdminEmail, defaultAdminPassword)
                            .addOnSuccessListener { authResult ->
                                val user = authResult.user
                                user?.let {
                                    val adminData = hashMapOf(
                                        "email" to defaultAdminEmail,
                                        "role" to Role.ADMIN.name
                                    )
                                    db.collection("users").document(defaultAdminEmail)
                                        .set(adminData)
                                }
                            }
                            .addOnFailureListener {
                                Log.e("AuthViewModel", "Failed to create default admin: ${it.message}")
                            }
                    } else {
                        Log.d("AuthViewModel", "Admin already exists.")
                    }
                }
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

                        val roleString = document.getString("role") ?: Role.PATIENT.name
                        val role = Role.valueOf(roleString)

                        when (role) {
                            Role.ADMIN -> {
                                _authState.value = AuthState.Authenticated(user, role)
                                Log.d("AuthViewModel", "Admin logged in")
                            }

                            Role.DOCTOR -> {
                                db.collection("doctors").document(userEmail).get()
                                    .addOnSuccessListener { doctorDocument ->
                                        if (doctorDocument.exists()) {
                                            val status = doctorDocument.getString("status") ?: "pending"
                                            when (status) {
                                                "approved" -> {
                                                    _authState.value = AuthState.Authenticated(user, role)
                                                    Log.d("AuthViewModel", "Doctor Approved: ${doctorDocument.data}")
                                                }
                                                "pending" -> {
                                                    _authState.value = AuthState.PendingApproval
                                                    Log.d("AuthViewModel", "Doctor Pending Approval")
                                                }
                                                "declined" -> {
                                                    val rejectionReason = doctorDocument.getString("rejectionReason") ?: "No reason provided"
                                                    _authState.value = AuthState.Rejected(rejectionReason)
                                                    Log.d("AuthViewModel", "Doctor Rejected: $rejectionReason")
                                                }
                                            }
                                        } else {
                                            _authState.value = AuthState.Error("Doctor profile not found")
                                        }
                                    }
                            }

                            Role.PATIENT -> {
                                db.collection("patients").document(userEmail).get()
                                    .addOnSuccessListener { patientDocument ->
                                        if (patientDocument.exists()) {
                                            _authState.value = AuthState.Authenticated(user, role)
                                            Log.d("AuthViewModel", "Patient data: ${patientDocument.data}")
                                        } else {
                                            _authState.value = AuthState.Error("Patient profile not found")
                                        }
                                    }
                            }
                        }
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
                    if (verificationTask.isSuccessful) {
                        checkEmailVerification()
                        _authState.value = AuthState.VerificationEmailSent
                    } else {
                        _authState.value = AuthState.Error(
                            "Failed to send verification email: ${verificationTask.exception?.message}"
                        )
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
                        _authState.value = AuthState.Authenticated(
                            user = authResult.user!!,
                            role
                        )
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

    fun loadUserData() {
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


    fun signOutUser(navController: NavHostController) {
        firebaseAuth.signOut()
        _authState.value = AuthState.Uninitialized
        navController.navigate(LoginScreen) {
            popUpTo(0) { inclusive = true }
        }
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