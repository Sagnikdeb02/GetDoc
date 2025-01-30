package com.example.getdoc.ui.patient.appointment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.getdoc.ui.patient.PatientViewModel
import com.example.getdoc.ui.patient.component.CustomButton
import com.example.getdoc.ui.patient.component.CustomAppBar

@Composable
fun AddPatientScreen(viewModel: PatientViewModel ,
                     onBackClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        CustomAppBar(title = "Add A Patient", onBackClick = onBackClick)

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
                label = { Text("Age (DD/MM/YYYY)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.relation,
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

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("Self", "Spouse", "Child", "Parent", "Other").forEach { option ->
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

            CustomButton(
                buttonText = "Save",
                onClick = { viewModel.submitAddPatient() }
            )
        }
    }
}
