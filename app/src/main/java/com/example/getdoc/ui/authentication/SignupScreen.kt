package com.example.getdoc.ui.authentication

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.getdoc.R
import kotlinx.coroutines.tasks.await
import java.util.regex.Pattern

@Composable
fun SignupScreen(
    viewModel: AuthenticationViewModel,
    onSignUpSuccess: () -> Unit
) {
    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val firebaseUser by viewModel.firebaseUser.collectAsStateWithLifecycle()


    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var signUpError by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(firebaseUser) {
        if (firebaseUser != null) {
            onSignUpSuccess()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Sign Up",
            fontSize = 26.sp,
            color = Color(0xFF174666),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Username Input
//        OutlinedTextField(
//            value = username,
//            onValueChange = {
//                username = it
//                usernameError = ""
//            },
//            label = { Text(text = "Create Username") },
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(20.dp),
//            isError = usernameError.isNotEmpty()
//        )
//        if (usernameError.isNotEmpty()) {
//            Text(text = usernameError, color = Color.Red, fontSize = 12.sp)
//        }

        Spacer(modifier = Modifier.height(16.dp))

        // Email Input
        OutlinedTextField(
            value = authUiState.email,
            onValueChange = {
                viewModel.onEmailChange(it.trim())
                emailError = ""
            },
            label = { Text(text = "Enter Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            isError = emailError.isNotEmpty()
        )
        if (emailError.isNotEmpty()) {
            Text(text = emailError, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        OutlinedTextField(
            value = authUiState.password,
            onValueChange = {
                viewModel.onPasswordChange(it.trim())
                passwordError = ""
            },
            label = { Text(text = "Create Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            isError = passwordError.isNotEmpty(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    val icon = if (passwordVisible) R.drawable.img_15 else R.drawable.img_2
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp) // Adjust the icon size here
                    )
                }
            }
        )
        if (passwordError.isNotEmpty()) {
            Text(text = passwordError, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Input
        OutlinedTextField(
            value = authUiState.confirmPassword,
            onValueChange = {
                viewModel.onConfirmPasswordChange(it.trim())
                confirmPasswordError = ""
            },
            label = { Text(text = "Re-Enter Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            isError = confirmPasswordError.isNotEmpty(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    val icon = if (passwordVisible) R.drawable.img_15 else R.drawable.img_2
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp) // Adjust the icon size here
                    )
                }
            }
        )
        if (confirmPasswordError.isNotEmpty()) {
            Text(text = confirmPasswordError, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up Button
        Button(
            onClick = {
//                usernameError = validateUsername(username)
                emailError = validateEmail(authUiState.email)
                passwordError = validatePassword(authUiState.password)
                confirmPasswordError = validateConfirmPassword(authUiState.password, authUiState.confirmPassword)

                if (emailError.isEmpty() && passwordError.isEmpty() && confirmPasswordError.isEmpty()) {
                    isLoading = true
                    viewModel.onSignUpClick()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color(0xFF174666)),
            shape = RoundedCornerShape(20.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Sign Up", color = Color.White, fontSize = 18.sp)
            }
        }

        if (signUpError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = signUpError, color = Color.Red, fontSize = 12.sp)
        }
    }
}

suspend fun createUser(auth: FirebaseAuth, email: String, password: String, context: Context) {
  try {
        val user = auth.createUserWithEmailAndPassword(email, password).await().user
        user?.sendEmailVerification()?.await()

      // Show toast for email verification
       Toast.makeText(context, "Check your email for email verification.", Toast.LENGTH_LONG).show()
   } catch (e: Exception) {
        throw e // Rethrow to handle in the calling function
    }
}

fun validateUsername(username: String): String {
    return if (username.isBlank()) "Username cannot be empty" else ""
}

fun validateEmail(email: String): String {
    val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"
    return if (!Pattern.compile(emailPattern).matcher(email).matches()) "Invalid email format " else ""
}

fun validatePassword(password: String): String {
    return when {
        password.isBlank() -> "Password cannot be empty"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> ""
    }
}

fun validateConfirmPassword(password: String, confirmPassword: String): String {
    return if (confirmPassword != password) "Passwords do not match" else ""
}
