package com.example.getdoc.ui.patient.appointment

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.getdoc.ui.patient.PatientViewModel
import com.example.getdoc.ui.patient.component.CustomButton
import com.example.getdoc.ui.patient.component.CustomAppBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddPatientScreen(
    viewModel: PatientViewModel,
    navController: NavController,
    firestore: FirebaseFirestore,
    doctorId: String,
    selectedDate: String,
    selectedSlot: String,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var expanded by remember { mutableStateOf(false) }
    val relationOptions = listOf("Self", "Spouse", "Child", "Parent", "Other")

    Scaffold(
        topBar = { CustomAppBar(title = "Add Patient", onBackClick = onBackClick) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF9F6))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Patient Information", style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.firstName,
                            onValueChange = { viewModel.updateUiState("firstName", it) },
                            label = { Text("First Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = uiState.lastName,
                            onValueChange = { viewModel.updateUiState("lastName", it) },
                            label = { Text("Last Name (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = uiState.age,
                            onValueChange = { viewModel.updateUiState("age", it) },
                            label = { Text("Age (Years)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Gender Selection
                        Text("Gender:", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        val genderOptions = listOf("Male", "Female", "Others")
                        LazyRow(verticalAlignment = Alignment.CenterVertically) {
                            items(genderOptions.size) { index ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .clickable { viewModel.updateUiState("gender", genderOptions[index]) }
                                ) {
                                    RadioButton(
                                        selected = uiState.gender == genderOptions[index],
                                        onClick = { viewModel.updateUiState("gender", genderOptions[index]) }
                                    )
                                    Text(genderOptions[index], modifier = Modifier.padding(start = 4.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Relation Selection (Dropdown)
                        Text("Relation", style = MaterialTheme.typography.bodyMedium)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = uiState.relation,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Relation") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { expanded = !expanded }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Icon")
                                    }
                                }
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                relationOptions.forEach { option ->
                                    DropdownMenuItem(
                                        onClick = {
                                            viewModel.updateUiState("relation", option)
                                            expanded = false
                                        },
                                        text = { Text(option) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Save Button
                        CustomButton(
                            buttonText = "Save Patient",
                            onClick = {
                                val patientData = hashMapOf(
                                    "firstName" to uiState.firstName,
                                    "lastName" to uiState.lastName,
                                    "age" to uiState.age,
                                    "gender" to uiState.gender,
                                    "relation" to uiState.relation
                                )

                                // Save patient info inside Firestore "appointments"
                                firestore.collection("appointments").document(doctorId)
                                    .collection(selectedDate).document(selectedSlot)
                                    .set(mapOf("patientInfo" to patientData))
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "Patient info saved in appointment")

                                        // Pass patient data back to ProceedScreen
                                        navController.previousBackStackEntry?.savedStateHandle?.set("patientInfo", patientData)

                                        navController.popBackStack() // Navigate back to ProceedScreen
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firestore", "Error saving patient info: ${e.message}")
                                    }
                            }
                        )
                    }
                }
            }
        }
    )
}
