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

@Composable
fun MyAppointmentsPreview() {
    Scaffold(
        bottomBar = {
//            BottomAppBar()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HeaderSection()
            TabSection2()
            AppointmentsList2()
        }
    }
}



@Composable
fun TabSection2() {
    val selectedTab = remember { mutableStateOf(1) }
    TabRow(selectedTabIndex = selectedTab.value) {
        Tab(
            selected = selectedTab.value == 0,
            onClick = { selectedTab.value = 0 }
        ) {
            Text(text = "Actives", modifier = Modifier.padding(16.dp))
        }
        Tab(
            selected = selectedTab.value == 1,
            onClick = { selectedTab.value = 1 }
        ) {
            Text(text = "Previews", modifier = Modifier.padding(16.dp))
        }
    }
}




@Composable
fun AppointmentCard(appointment: Appointment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = appointment.daysAgo, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = appointment.doctorName, style = MaterialTheme.typography.headlineSmall)
            Text(text = "(${appointment.specialty})", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Date: ${appointment.date}", fontSize = 14.sp)
            Text(text = "Time/Token: ${appointment.timeToken}", fontSize = 14.sp)
            Text(text = "Place: ${appointment.place}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* Add Review Logic */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text(text = "Add A Review", color = Color.White)
            }
        }
    }
}



data class Appointment(
    val doctorName: String,
    val specialty: String,
    val date: String,
    val timeToken: String,
    val place: String,
    val daysAgo: String
)

@Composable
fun AppointmentsList2() {
    val appointments = listOf(
        Appointment(
            doctorName = "Dr Priya Hasan",
            specialty = "Cardiologist",
            date = "15/02/2026",
            timeToken = "08:30 AM",
            place = "Serum Clinic, Rose Dam, Near Police Station, Sylhet",
            daysAgo = "3 Days Ago"
        ),
        Appointment(
            doctorName = "Dr Anil",
            specialty = "Cardiologist",
            date = "15/02/2026",
            timeToken = "32",
            place = "Serum Clinic, Rose Dam, Near Police Station, Sylhet",
            daysAgo = "8 Days Ago"
        ),
        Appointment(
            doctorName = "Dr Karim",
            specialty = "Cardiologist",
            date = "15/02/2026",
            timeToken = "08:30 AM",
            place = "Serum Clinic, Rose Dam, Near Police Station, West Harm",
            daysAgo = "12 Days Ago"
        )
    )

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(items = appointments) { appointment ->
            AppointmentCard(appointment)
        }
    }
}


@Preview
@Composable
private fun MyAppointmentsPreviewpre() {
    MyAppointmentsPreview()
}