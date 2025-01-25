package com.example.getdoc.ui.doctor.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.getdoc.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.getdoc.ui.doctor.DoctorViewModel
import com.example.getdoc.ui.doctor.appointments.HeaderComponent

/**
 * A composable function representing the credentials page for a user.
 *
 * @param onHomeClick Lambda for handling the "Home" button click in the bottom bar.
 * @param onAppointmentsClick Lambda for handling the "Appointments" button click in the bottom bar.
 * @param onProfileClick Lambda for handling the "Profile" button click in the bottom bar.
 */
@Composable
fun MyCredentialsPageComponent(
    viewModel: DoctorViewModel ,
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F3F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderComponent(title = "My Credentials", iconResId = R.drawable.img_9)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                FormFieldComponent(
                    label = "Enter Your Full Name",
                    value = uiState.name,
                    onValueChange = { viewModel.updateUiState("name", it) }
                )
            }
            item {
                FormFieldComponent(
                    label = "Enter Your Degree",
                    value = uiState.degree,
                    onValueChange = { viewModel.updateUiState("degree", it) }
                )
            }
            item {
                FormFieldComponent(
                    label = "Enter Your Speciality",
                    value = uiState.speciality,
                    onValueChange = { viewModel.updateUiState("speciality", it) }
                )
            }
            item {
                FormFieldComponent(
                    label = "Enter Your DOB",
                    value = uiState.dob,
                    onValueChange = { viewModel.updateUiState("dob", it) }
                )
            }
            item {
                FormFieldComponent(
                    label = "Enter Your Clinic Address",
                    value = uiState.address,
                    onValueChange = { viewModel.updateUiState("address", it) }
                )
            }
            item {
                FormFieldComponent(
                    label = "Enter Your Consultation Fee",
                    value = uiState.fee,
                    onValueChange = { viewModel.updateUiState("fee", it) }
                )
            }
            item {
                FormFieldComponent(
                    label = "About You",
                    value = uiState.aboutYou,
                    onValueChange = { viewModel.updateUiState("aboutYou", it) }
                )
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(Color(0xFF174666))
                    ) {
                        Text(text = "Cancel")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { viewModel.submitDoctorCredentialProfile()},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(Color(0xFF174666))
                    ) {
                        Text(text = "Submit")
                    }
                }
            }

            item {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                }
            }

            item {
                uiState.errorMessage?.let {
                    Text(text = it, color = Color.Red)
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirm Cancel") },
                text = { Text("Are you sure you want to clear the form?") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.clearForm()
                        showDialog = false
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("No")
                    }
                }
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            BottomBarComponent(
                onHomeClick = onHomeClick,
                onAppointmentsClick = onAppointmentsClick,
                onProfileClick = onProfileClick
            )
        }
    }
}

@Composable
fun FormFieldComponent(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        BasicTextField(
            value = value, // Directly use the state passed from ViewModel
            onValueChange = onValueChange, // Update ViewModel state
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    BorderStroke(width = 1.dp, color = Color.Gray),
                    shape = RoundedCornerShape(20.dp)
                )
                .background(Color.White)
                .padding(8.dp)
                .height(40.dp),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            )
        )
    }
}
  