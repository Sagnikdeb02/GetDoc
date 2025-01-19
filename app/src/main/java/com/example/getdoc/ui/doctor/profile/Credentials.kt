package com.example.getdoc.ui.doctor.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.getdoc.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
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
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // States for the form fields
    val degree = remember { mutableStateOf(TextFieldValue("")) }
    val speciality = remember { mutableStateOf(TextFieldValue("")) }
    val dob = remember { mutableStateOf(TextFieldValue("")) }
    val address = remember { mutableStateOf(TextFieldValue("")) }
    val fee = remember { mutableStateOf(TextFieldValue("")) }
    val aboutYou = remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F3F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Section
        HeaderComponent(title = "My Credentials", iconResId = R.drawable.img_9)

        // Form Fields Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            FormFieldComponent(label = "Enter Your Degree", value = degree.value, onValueChange = { degree.value = it })
            FormFieldComponent(label = "Enter Your Speciality", value = speciality.value, onValueChange = { speciality.value = it })
            FormFieldComponent(label = "Enter Your DOB", value = dob.value, onValueChange = { dob.value = it })
            FormFieldComponent(label = "Enter Your Clinic Address", value = address.value, onValueChange = { address.value = it })
            FormFieldComponent(label = "Enter Your Consultation Fee", value = fee.value, onValueChange = { fee.value = it })
            FormFieldComponent(label = "About You", value = aboutYou.value, onValueChange = { aboutYou.value = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons Section
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* Cancel action */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(Color(0xFF174666))
                ) {
                    Text(text = "Cancel")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { /* Submit action */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(Color(0xFF174666))
                ) {
                    Text(text = "Submit")
                }
            }
        }

        // Bottom Bar Section
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

/**
 * A reusable composable for a form field.
 *
 * @param label The label text for the form field.
 * @param value The current text field value.
 * @param onValueChange Lambda to handle changes in the text field.
 * @param modifier Modifier for styling the form field.
 */
@Composable
fun FormFieldComponent(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = label, fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    BorderStroke(width = 1.dp, color = Color.Gray),
                    shape = RoundedCornerShape(20.dp)
                )
                .background(Color.White)
                .padding(8.dp)
                .height(40.dp)
        )
    }
}

/**
 * Preview function for MyCredentialsPageComponent.
 */
@Preview(showSystemUi = true)
@Composable
fun PreviewMyCredentialsPageComponent() {
    MyCredentialsPageComponent(
        onHomeClick = { /* Navigate to Home */ },
        onAppointmentsClick = { /* Navigate to Appointments */ },
        onProfileClick = { /* Navigate to Profile */ }
    )
}




