package com.example.getdoc.ui.patient.appointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.data.model.AppointmentInfo
import com.example.getdoc.ui.patient.component.PatientBottomBarComponent
import com.example.getdoc.ui.patient.component.PreviousAppointmentComponent

@Composable
fun PreviousAppointmentsScreen(
    selectedTabIndex: Int,
    onTabChange: (Int) -> Unit,
    appointments: List<AppointmentInfo>,
    onAddReviewClick: (AppointmentInfo) -> Unit,
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onDoctorsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            PatientBottomBarComponent(
                onHomeClick = onHomeClick,
                onAppointmentsClick = onAppointmentsClick,
                onDoctorsClick = onDoctorsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HeaderSection()
            TabSection2(
                selectedTabIndex = selectedTabIndex,
                onTabChange = onTabChange
            )
            PreviousAppointmentsList(
                appointments = appointments,
                onAddReviewClick = onAddReviewClick
            )
        }
    }
}

@Composable
fun TabSection2(
    selectedTabIndex: Int,
    onTabChange: (Int) -> Unit
) {
    TabRow(selectedTabIndex = selectedTabIndex) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = { onTabChange(0) }
        ) {
            Text(text = "Actives", modifier = Modifier.padding(16.dp))
        }
        Tab(
            selected = selectedTabIndex == 1,
            onClick = { onTabChange(1) }
        ) {
            Text(text = "Previous", modifier = Modifier.padding(16.dp))
        }
    }
}



@Composable
fun PreviousAppointmentsList(
    appointments: List<AppointmentInfo>,
    onAddReviewClick: (AppointmentInfo) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(items = appointments) { appointment ->
            PreviousAppointmentComponent(
                appointment = appointment,
                onAddReviewClick = { onAddReviewClick(appointment) }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviousAppointmentsScreenPreview() {
    // Mock data for appointments
    val mockAppointments = listOf(
        AppointmentInfo(
            doctorName = "Dr. Priya Hasan",
            specialty = "Cardiologist",
            date = "15/01/2025",
            time = "10:00 AM",
            place = "Serum Clinic, Rose Dam, Near Police Station, Sylhet",
            daysCount = "5 Days Ago"
        ),
        AppointmentInfo(
            doctorName = "Dr. Anil Kumar",
            specialty = "Dermatologist",
            date = "10/01/2025",
            time = "02:00 PM",
            place = "HealthCare Center, Main Road, Chittagong",
            daysCount = "10 Days Ago"
        )
    )

    // Preview the composable
    PreviousAppointmentsScreen(
        selectedTabIndex = 1, // "Previous" tab selected
        onTabChange = { selectedTab ->
            println("Tab changed to index: $selectedTab")
        },
        appointments = mockAppointments,
        onAddReviewClick = { appointment ->
            println("Add Review clicked for: ${appointment.doctorName}")
        },
        onHomeClick = {},
        onAppointmentsClick = {},
        onDoctorsClick = {},
        onProfileClick = {},
    )
}
