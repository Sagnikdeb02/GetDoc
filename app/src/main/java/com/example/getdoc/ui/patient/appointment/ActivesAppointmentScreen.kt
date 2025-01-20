package com.example.getdoc.ui.patient.appointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.getdoc.data.model.AppointmentInfo
import com.example.getdoc.ui.patient.component.ActiveAppointmentComponent
import com.example.getdoc.ui.patient.component.PatientBottomBarComponent


@Composable
fun ActivesAppointmentScreen(
    selectedTabIndex: Int,
    onTabChange: (Int) -> Unit,
    appointments: List<AppointmentInfo>,
    onRescheduleClick: (AppointmentInfo) -> Unit,
    onCancelClick: (AppointmentInfo) -> Unit,
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
            TabSection(
                selectedTabIndex = selectedTabIndex,
                onTabChange = onTabChange
            )
            AppointmentsList(
                appointments = appointments,
                onRescheduleClick = onRescheduleClick,
                onCancelClick = onCancelClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSection() {
    TopAppBar(
        title = { Text(text = "My Appointments") }
    )
}

@Composable
fun TabSection(
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
            Text(text = "Previews", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun AppointmentsList(
    appointments: List<AppointmentInfo>,
    onRescheduleClick: (AppointmentInfo) -> Unit,
    onCancelClick: (AppointmentInfo) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(appointments) { appointment ->
            ActiveAppointmentComponent(
                appointment = appointment,
                onRescheduleClick = { onRescheduleClick(appointment) },
                onCancelClick = { onCancelClick(appointment) }
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun MyAppointmentsPagePreview() {
    // Mock data for appointments
    val mockAppointments = listOf(
        AppointmentInfo(
            doctorName = "Dr. Priya Hasan",
            specialty = "Cardiologist",
            date = "15/02/2026",
            time = "08:30 AM",
            place = "Serum Clinic, Rose Dam, Near Police Station, Sylhet",
            daysCount = "2 Days Remaining"
        ),
        AppointmentInfo(
            doctorName = "Dr. Anil Kumar",
            specialty = "Dermatologist",
            date = "20/02/2026",
            time = "10:00 AM",
            place = "HealthCare Center, Main Road, Chittagong",
            daysCount = "5 Days Remaining"
        )
    )

    // Preview the page
    ActivesAppointmentScreen(
        selectedTabIndex = 0,
        onTabChange = { /* Handle tab change */ },
        appointments = mockAppointments,
        onRescheduleClick = { appointment ->
            println("Reschedule clicked for: ${appointment.doctorName}")
        },
        onCancelClick = { appointment ->
            println("Cancel clicked for: ${appointment.doctorName}")
        },
        onHomeClick = {},
        onAppointmentsClick = {},
        onDoctorsClick = {},
        onProfileClick = {},
    )
}


