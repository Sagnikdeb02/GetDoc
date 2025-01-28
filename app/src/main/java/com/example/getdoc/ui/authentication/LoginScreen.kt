package com.example.getdoc.ui.authentication

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.getdoc.R
import com.example.getdoc.navigation.AdminHomeScreen
import com.example.getdoc.navigation.ChooseRoleScreen
import com.example.getdoc.navigation.DoctorHomeScreen
import com.example.getdoc.navigation.LoginScreen
import com.example.getdoc.navigation.PatientHomeScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.regex.Pattern


// Preference keys
private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
private val USER_ROLE_KEY = stringPreferencesKey("user_role")
private val USER_EMAIL_KEY = stringPreferencesKey("user_email")

@Composable
fun LogInScreen(
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    navController: NavHostController,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Auto-login if session exists
    LaunchedEffect(Unit) {
        val preferences = context.dataStore.data.first()
        val isLoggedIn = preferences[IS_LOGGED_IN_KEY] ?: false
        val savedRole = preferences[USER_ROLE_KEY] ?: ""

        if (isLoggedIn) {
            when (savedRole) {
                "admin" -> navController.navigate(AdminHomeScreen)
                "doctor" -> navController.navigate(DoctorHomeScreen)
                "patient" -> navController.navigate(PatientHomeScreen)
            }
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

                        signInAdmin(email, password, context) { role ->
                            isLoading = false
                            coroutineScope.launch {
                                saveLoginSession(context, email, role)
                            }
                            onLoginSuccess()
                            navController.navigate(AdminHomeScreen) { popUpTo(LoginScreen) { inclusive = true } }
                        }

                        signInDoctor(firestore, email, password, context) { role ->
                            isLoading = false
                            coroutineScope.launch {
                                saveLoginSession(context, email, role)
                            }
                            onLoginSuccess()
                            navController.navigate(DoctorHomeScreen) { popUpTo(LoginScreen) { inclusive = true } }
                        }

                        signInPatient(auth, email, password, context) { role ->
                            isLoading = false
                            coroutineScope.launch {
                                saveLoginSession(context, email, role)
                            }
                            onLoginSuccess()
                            navController.navigate(PatientHomeScreen) { popUpTo(LoginScreen) { inclusive = true } }
                        }
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
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Don't have an account? ")
                ClickableText(
                    text = AnnotatedString("Create one"),
                    onClick = { navController.navigate(ChooseRoleScreen) }
                )
            }
        }
    }
}


// Save user session in DataStore
suspend fun saveLoginSession(context: Context, email: String, role: String) {
    context.dataStore.edit { preferences ->
        preferences[IS_LOGGED_IN_KEY] = true
        preferences[USER_ROLE_KEY] = role
        preferences[USER_EMAIL_KEY] = email
    }
}

// Helper function to validate email format
fun isValidEmail(email: String): Boolean {
    val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    return Pattern.compile(emailPattern).matcher(email).matches()
}

fun signInAdmin(
    email: String,
    password: String,
    context: Context,
    onSignInSuccess: (String) -> Unit
) {
    if (email.trim().lowercase() == "admin@getdoc.com" && password == "admin123") {
        onSignInSuccess("admin")
        Toast.makeText(context, "Admin login successful!", Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(context, "Invalid admin credentials", Toast.LENGTH_LONG).show()
    }
}

fun signInDoctor(
    firestore: FirebaseFirestore,
    email: String,
    password: String,
    context: Context,
    onSignInSuccess: (String) -> Unit
) {
    firestore.collection("doctor_registrations")
        .whereEqualTo("email", email)
        .whereEqualTo("password", password) // Checking directly as per your requirements
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val document = documents.documents[0]
                val status = document.getString("status") ?: "pending"
                if (status == "approved") {
                    onSignInSuccess("doctor")
                    Toast.makeText(context, "Doctor login successful!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Your registration is pending approval", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Doctor not found or incorrect credentials", Toast.LENGTH_LONG).show()
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error fetching registration", Toast.LENGTH_LONG).show()
        }
}

fun signInPatient(
    auth: FirebaseAuth,
    email: String,
    password: String,
    context: Context,
    onSignInSuccess: (String) -> Unit
) {
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val user = auth.currentUser
            if (user?.isEmailVerified == true) {
                onSignInSuccess("patient")
                Toast.makeText(context, "Patient login successful!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Please verify your email first", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Sign-in failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
        }
    }
}
