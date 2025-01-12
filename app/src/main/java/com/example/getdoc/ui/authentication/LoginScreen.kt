package com.example.getdoc.ui.authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.example.getdoc.R
import java.util.regex.Pattern



// LogIn Composable Screen
@Composable
fun LogInScreen(
    viewModel: AuthenticationViewModel,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
//    onForgotPasswordClick: () -> Unit
) {
    val context = LocalContext.current
    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val firebaseUser by viewModel.firebaseUser.collectAsStateWithLifecycle()

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = firebaseUser) {
        if (firebaseUser != null) {
            onLoginSuccess()
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
            Text(
                text = "Sign In",
                fontSize = 26.sp,
                color = Color(0xFF174666),
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = authUiState.email,
                onValueChange = {
                    viewModel.onEmailChange(it.trim())
                    emailError = ""
                },
                label = { Text(text = "Enter Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                isError = emailError.isNotEmpty(),
            )
            if (emailError.isNotEmpty()) {
                Text(text = emailError, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = authUiState.password,
                onValueChange = {
                    viewModel.onPasswordChange(it.trim())
                    passwordError = ""
                },
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

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    if (authUiState.email.isNotEmpty()) {
                        isLoading = true
                        viewModel.onPasswordResetClick()
                    } else {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_LONG).show()
                    }
                }) {
                    Text(text = "Forgot password?", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            emailError = ""
                            passwordError = ""

                            if (!isValidEmail(authUiState.email)) {
                                emailError = "Invalid email format"
                            }
                            if (authUiState.password.isEmpty()) {
                                passwordError = "Password cannot be empty"
                            } else if (authUiState.password.length < 6) {
                                passwordError = "Password must be at least 6 characters"
                            }

                            if (emailError.isEmpty() && passwordError.isEmpty()) {
                                isLoading = true

                                viewModel.onSignInClick()
                            }

                        },
                        modifier = Modifier.size(height = 50.dp, width = 120.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF174666)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(text = "Sign In", fontSize = 20.sp, color = Color.White)
                    }
                }
            }

            if (loginError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = loginError, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Don't have an account? ")
                ClickableText(
                    text = AnnotatedString("Create one"),
                    onClick = {onSignUpClick()}
                )
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"
    return Pattern.compile(emailPattern).matcher(email).matches()
}

@Preview(showBackground = true)
@Composable
fun LogInPreview() {
    val mockAuth = FirebaseAuth.getInstance()
    val mockNavController = rememberNavController()

//    LogInScreen(
//        auth = mockAuth,
//        navController = mockNavController,
//        onLoginSuccess = { /* Handle login success */ }
//    )
}
// Create User function
//suspend fun createUser(auth: FirebaseAuth, email: String, password: String, context: Context) {
//    try {
//        val user = auth.createUserWithEmailAndPassword(email, password).await().user
//        user?.sendEmailVerification()?.await()
//        Toast.makeText(context, "Check your email for email verification.", Toast.LENGTH_LONG).show()
//    } catch (e: Exception) {
//        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
//    }
//}
// Updated signInUser function
//fun signInUser(
//    auth: FirebaseAuth,
//    email: String,
//    password: String,
//    context: Context, // Pass Context here
//    onSignInSuccess: () -> Unit
//) {
//    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
//        if (task.isSuccessful) {
//            val user = auth.currentUser
//            if (user?.isEmailVerified == true) {
//                onSignInSuccess()
//            } else {
//                Toast.makeText(context, "Please verify your email first", Toast.LENGTH_LONG).show()
//            }
//        } else {
//            Toast.makeText(context, "Sign-in failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
//        }
//    }
//}
