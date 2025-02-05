package com.example.getdoc.ui.authentication


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.getdoc.R
import java.util.regex.Pattern



@Composable
fun LogInScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (role: Role) -> Unit,
    onSignUpClick: () -> Unit,
    onVerificationEmailSent: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                val userRole = (authState as AuthState.Authenticated).role
                when (userRole) {
                    Role.ADMIN -> onLoginSuccess(Role.ADMIN)
                    Role.DOCTOR -> onLoginSuccess(Role.DOCTOR)
                    Role.PATIENT -> onLoginSuccess(Role.PATIENT)
                }
            }

            is AuthState.Error -> {}
            AuthState.Loading -> {}
            AuthState.PendingApproval -> {}
            is AuthState.Rejected -> {}
            AuthState.Uninitialized -> {}
            AuthState.VerificationEmailSent -> onVerificationEmailSent()
        }
    }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Sign In", fontSize = 26.sp, color = Color(0xFF174666))

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim(); emailError = "" },
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
                onValueChange = { password = it.trim(); passwordError = "" },
                label = { Text(text = "Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        val icon = if (passwordVisible) R.drawable.img_15 else R.drawable.img_2
                        Icon(painter = painterResource(id = icon), contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                isError = passwordError.isNotEmpty()
            )
            if (passwordError.isNotEmpty()) {
                Text(text = passwordError, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    emailError = ""
                    passwordError = ""

                    if (!isValidEmail(email)) {
                        emailError = "Invalid email format"
                    }
                    if (password.isEmpty()) {
                        passwordError = "Password cannot be empty"
                    } else if (password.length < 6) {
                        passwordError = "Password must be at least 6 characters"
                    }

                    if (emailError.isEmpty() && passwordError.isEmpty()) {
                        isLoading = true
                        viewModel.signInUser(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0xFF174666)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = if (isLoading) "Signing In..." else "Sign In", fontSize = 20.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onForgotPasswordClick
                ) {
                    Text(text = "Forgot Password?", color = Color(0xFF174666))

                }
                TextButton(
                    onClick = onSignUpClick
                ) {
                    Text(text = "Sign Up", color = Color(0xFF174666))
                }
            }
        }
    }
}



// Helper function to validate email format
fun isValidEmail(email: String): Boolean {
    val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    return Pattern.compile(emailPattern).matcher(email).matches()
}