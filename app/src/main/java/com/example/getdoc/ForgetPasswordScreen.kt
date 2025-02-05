package com.example.getdoc

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ForgetPasswordScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Forgot Password?",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Enter your registered email to receive a password reset link.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp))
        } else {
            Button(
                onClick = {
                    if (email.text.isEmpty()) {
                        errorMessage = "Please enter your email."
                    } else {
                        isLoading = true
                        coroutineScope.launch {
                            resetPassword(auth, email.text,
                                onSuccess = {
                                    isLoading = false
                                    successMessage = "Password reset link sent. Check your email."
                                    errorMessage = null
                                },
                                onFailure = { error ->
                                    isLoading = false
                                    errorMessage = error
                                    successMessage = null
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset Password")
            }
        }

        successMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("LoginScreen") }) {
            Text("Back to Login")
        }
    }
}

private fun resetPassword(
    auth: FirebaseAuth,
    email: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    auth.sendPasswordResetEmail(email)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Failed to send reset email.")
        }
}
