package com.example.getdoc

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.getdoc.navigation.LoginScreen
import com.example.getdoc.ui.authentication.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
@Composable
fun DeleteAccountScreen(
    navController: NavHostController, // Pass the NavController from parent
    viewModel: AuthViewModel = viewModel(),
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val user = auth.currentUser

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Delete Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Deleting your account is permanent and cannot be undone. All your data will be removed.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isDeleting) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp))
        } else {
            Button(
                onClick = { showConfirmationDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete My Account")
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmationDialog = false
                        isDeleting = true
                        coroutineScope.launch {
                            deleteAccount(
                                user,
                                firestore,
                                onSuccess = {
                                    isDeleting = false
                                    coroutineScope.launch {
                                        onAccountDeleted(navController)
                                    }
                                },
                                onFailure = { error ->
                                    isDeleting = false
                                    errorMessage = error
                                }
                            )
                        }
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Are you sure?") },
            text = { Text("This action cannot be undone. Do you really want to delete your account?") }
        )
    }
}

private fun deleteAccount(
    user: FirebaseUser?,
    firestore: FirebaseFirestore,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    user?.let { firebaseUser ->
        val userEmail = firebaseUser.email

        if (userEmail != null) {
            firestore.collection("users").document(userEmail).delete()
                .addOnSuccessListener {
                    firebaseUser.delete()
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            onFailure("Failed to delete account: ${exception.message}")
                        }
                }
                .addOnFailureListener { exception ->
                    onFailure("Failed to delete user data: ${exception.message}")
                }
        } else {
            onFailure("User email not found.")
        }
    } ?: run {
        onFailure("User not authenticated.")
    }
}

private fun onAccountDeleted(navController: NavHostController) {
    navController.navigate(LoginScreen) {
        popUpTo(0) { inclusive = true }
    }
}
