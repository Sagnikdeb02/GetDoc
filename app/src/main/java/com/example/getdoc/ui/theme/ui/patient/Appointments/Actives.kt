package com.example.getdoc.ui.theme.ui.patient.Appointments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.R

@Composable
fun MyAppointmentsPage() {
    Scaffold(
        bottomBar = { BottomAppBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HeaderSection()
            TabSection()
            AppointmentsList()
        }
    }
}

@Composable
fun HeaderSection() {
    TopAppBar(
        title = { Text(text = "My Appointments") },
        backgroundColor = MaterialTheme.colors.primary
    )
}

@Composable
fun TabSection() {
    val selectedTab = remember { mutableStateOf(0) }
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
fun AppointmentsList() {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items((1..2).toList()) {
            AppointmentCard(
                doctorName = if (it == 1) "Dr Sagnik" else "Dr Priya Hasan",
                specialty = "Cardiologist",
                date = "15/02/2026",
                time = "08:30 AM",
                place = "Serum Clinic, Rose Dam, Near Police Station, Sylhet",
                daysRemaining = "2 Days Remaining"
            )
        }
    }
}

@Composable
fun AppointmentCard(
    doctorName: String,
    specialty: String,
    date: String,
    time: String,
    place: String,
    daysRemaining: String
) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder image
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = doctorName, style = MaterialTheme.typography.h6)
                    Text(text = "($specialty)", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "Date: $date", fontSize = 14.sp)
                    Text(
                        text = daysRemaining,
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Time/Token: $time", fontSize = 14.sp, color = Color.Gray)
            Text(text = "Place: $place", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                //horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),

                    onClick = { /* Reschedule logic */ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
                ) {
                    Text(text = "Reschedule", color = Color.White)
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* Cancel logic */ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                ) {
                    Text(text = "Cancel", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun BottomAppBar() {
    BottomNavigation {
        BottomNavigationItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(painterResource(id = R.drawable.home), contentDescription = "Home")
            },
            label = { Text(text = "Home") }
        )
        BottomNavigationItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(painterResource(id = R.drawable.appoinments), contentDescription = "Appointments")
            },
            label = { Text(text = "Appoinment") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(painterResource(id = R.drawable.doctors), contentDescription = "All doctors")
            },
            label = { Text(text = "Profile") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(painterResource(id = R.drawable.profile), contentDescription = "Profile")
            },
            label = { Text(text = "Profile") }
        )
    }
}


@Preview
@Composable
private fun MyAppointmentsPagePrev() {
    MyAppointmentsPage()
}
