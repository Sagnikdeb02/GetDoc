package com.example.getdoc.ui.authentication

import androidx.lifecycle.ViewModel
import com.example.getdoc.navigation.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class AuthenticationViewModel: ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    var authUiState = MutableStateFlow(AuthUiState())
        private set

    var firebaseUser = MutableStateFlow<FirebaseUser?>(null)

    init {
        firebaseUser.value = firebaseAuth.currentUser
    }

    fun onEmailChange(email: String) {
        authUiState.update {
            it.copy(email = email)
        }
    }
    fun onPasswordChange(password: String) {
        authUiState.update {
            it.copy(password = password)
        }
    }
    fun onConfirmPasswordChange(confirmPassword: String) {
        authUiState.update {
            it.copy(confirmPassword = confirmPassword)
        }
    }
    fun onRoleChange(role: Role) {
        authUiState.update {
            it.copy(role = role)
        }
    }

    fun onSignUpClick() {
        firebaseAuth.createUserWithEmailAndPassword(
            authUiState.value.email,
            authUiState.value.password
        ).addOnSuccessListener {
            firebaseUser.value = it.user
        }.addOnFailureListener {
            firebaseUser.value = null
        }
    }


    fun onSignInClick() {
        firebaseAuth.signInWithEmailAndPassword(
            authUiState.value.email,
            authUiState.value.password
        ).addOnSuccessListener {
            firebaseUser.value = it.user
        }.addOnFailureListener {
            firebaseUser.value = null
        }
    }
    fun onPasswordResetClick() {
        firebaseAuth.sendPasswordResetEmail(authUiState.value.email)
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
            }
    }

}


data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val role: Role? = null
)