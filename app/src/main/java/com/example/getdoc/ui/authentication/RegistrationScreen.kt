package com.example.getdoc.ui.authentication

import androidx.compose.foundation.Image
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
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.getdoc.R
import java.util.regex.Pattern

@Composable
fun RegistrationScreen(
    role: Role,
    viewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var signUpError by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is AuthState.VerificationEmailSent) {
            onSignUpSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Sign Up",
            fontSize = 26.sp,
            color = Color(0xFF174666),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
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

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
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
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
        if (passwordError.isNotEmpty()) {
            Text(text = passwordError, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
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
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
        if (confirmPasswordError.isNotEmpty()) {
            Text(text = confirmPasswordError, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                emailError = validateEmail(email)
                passwordError = validatePassword(password)
                confirmPasswordError = validateConfirmPassword(password, confirmPassword)

                if (emailError.isEmpty() && passwordError.isEmpty() && confirmPasswordError.isEmpty()) {
                    isLoading = true
                    viewModel.signUpUser(email, password, role, context)
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
    }
}

fun validatePassword(password: String): String {
    val passwordPattern = "^(?=.*[0-9])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{6,}$"
    return if (!Pattern.compile(passwordPattern).matcher(password).matches()) "Password must be at least 6 characters, include a number, and a special character" else ""
}



fun validateEmail(email: String): String {
    val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"
    return if (!Pattern.compile(emailPattern).matcher(email).matches()) "Invalid email format" else ""
}


fun validateConfirmPassword(password: String, confirmPassword: String): String {
    return if (confirmPassword != password) "Passwords do not match" else ""
}