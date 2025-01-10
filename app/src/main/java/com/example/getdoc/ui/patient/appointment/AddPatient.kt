package com.example.getdoc.ui.patient.appointment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.getdoc.data.model.PatientInfo
import com.example.getdoc.ui.patient.component.CustomButton
import com.example.getdoc.ui.patient.component.CustomAppBar

// Main AddPatientScreen
@Composable
fun AddPatientScreen(onSave: (PatientInfo) -> Unit, onBackClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Add AppBar
        CustomAppBar(
            title = "Add A Patient",
            onBackClick = onBackClick
        )

        // Patient Details Form
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var gender by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // First Name Field
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Last Name Field
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Age Field
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age (DD/MM/YYYY)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Gender Selection
            Text("Gender:", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(verticalAlignment = Alignment.CenterVertically) {
                items(listOf("Male", "Female", "Others")) { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { gender = option }
                    ) {
                        RadioButton(
                            selected = gender == option,
                            onClick = { gender = option }
                        )
                        Text(option, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Relation Dropdown
            var selectedRelation by remember { mutableStateOf("Self") }
            var expanded by remember { mutableStateOf(false) }
            val relationOptions = listOf("Self", "Spouse", "Child", "Parent", "Other")

            Box(modifier = Modifier.fillMaxWidth()) {
                // TextField as a dropdown trigger
                OutlinedTextField(
                    value = selectedRelation,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Relation") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Icon")
                        }
                    }
                )

                // DropdownMenu with alternative implementation
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    relationOptions.forEach { option ->
                        // Each option is wrapped in a Row with clickable modifier
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    selectedRelation = option
                                    expanded = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Instructions
            Text(
                text = "• Add Patient Basic Information For Know About Patient Basic Details\n" +
                        "• And Doctors Right Prescriptions. Please Given A Proper Details About Patient",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            CustomButton(
                buttonText = "Save",
                onClick = { /* Handle book appointment logic */ }
            )
        }
    }
}



// Usage Example
@Preview(showSystemUi = true)
@Composable
fun AddPatientScreenPreview() {
    MaterialTheme {
        AddPatientScreen(
            onSave = { patient -> println("Saved patient: $patient") },
            onBackClick = { println("Back button clicked") }
        )
    }
}
