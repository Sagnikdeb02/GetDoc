package com.example.getdoc.ui.doctor.appointments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.R
import com.example.getdoc.ui.doctor.profile.BottomBarComponent
import com.example.getdoc.ui.doctor.profile.BottomBarItem

/**
 * A dynamic composable that displays patient details along with a bottom bar.
 *
 * @param patientDetails The state containing all patient details to display.
 * @param patientType The type of the patient (e.g., "Old Patient").
 * @param onHomeClick Lambda for handling the Home button click in the bottom bar.
 * @param onAppointmentsClick Lambda for handling the My Appointments button click.
 * @param onProfileClick Lambda for handling the Profile button click.
 * @param modifier Modifier for styling the overall layout.
 */
@Composable
fun PatientDetailsPageComponent(
    patientDetails: List<Pair<String, String>>,
    patientType: String,
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F7)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Section
        HeaderComponent(title = "Patient Details", iconResId = R.drawable.img_9)

        // Patient Details Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(Color(0xFFA1DFD8), shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                patientDetails.forEach { (label, value) ->
                    PatientDetailRowComponent(label = label, value = value)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Patient Type Section
        Text(
            text = "Patient Type: $patientType",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1565C0),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

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
 * A placeholder composable for the bottom bar with three actions.
 *
 * @param onHomeClick Lambda for handling the Home button click.
 * @param onAppointmentsClick Lambda for handling the My Appointments button click.
 * @param onProfileClick Lambda for handling the Profile button click.
 * @param modifier Modifier for styling the bottom bar.
 */
@Composable
fun BottomBarComponent(
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        BottomBarItem(iconResId = R.drawable.img_6, label = "Home", onClick = onHomeClick)
        BottomBarItem(iconResId = R.drawable.img_8, label = "My Appointments", onClick = onAppointmentsClick)
        BottomBarItem(iconResId = R.drawable.img_10, label = "Profile", onClick = onProfileClick)
    }
}


/**
 * A reusable composable for displaying the header of the screen.
 *
 * @param title The title text of the header.
 * @param iconResId The resource ID of the icon to display on the right.
 * @param modifier Modifier for styling the header.
 */
@Composable
fun HeaderComponent(
    title: String,
    iconResId: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFF1565C0))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            color = Color.White
        )
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * A reusable composable for displaying the patient detail rows.
 *
 * @param label The label for the patient detail.
 * @param value The value associated with the label.
 * @param modifier Modifier for styling the row.
 */
@Composable
fun PatientDetailRowComponent(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    }
}

// Main Layout Section
@Composable
fun PatientDetailsLayout(
    patientDetails: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(Color(0xFFA1DFD8), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        patientDetails.forEach { (label, value) ->
            PatientDetailRowComponent(label = label, value = value)
        }
    }
}


/**
 * A reusable composable for previewing PatientDetailsPage.
 */
@Preview(showSystemUi = true)
@Composable
fun PreviewPatientDetailsPage() {
    PatientDetailsPageComponent(
        patientDetails = listOf(
            "Name" to "Sagnik Deb",
            "Gender" to "Male",
            "Age" to "32 Year Old",
            "Booking For" to "Self",
            "Booking ID" to "100068",
            "Phone No" to "01711111111"
        ),
        patientType = "Old Patient",
        onHomeClick = { /* Handle Home click */ },
        onAppointmentsClick = { /* Handle My Appointments click */ },
        onProfileClick = { /* Handle Profile click */ }
    )
}
