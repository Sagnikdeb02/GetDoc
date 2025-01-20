package com.example.getdoc.ui.patient

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.ui.patient.component.DoctorCard
import com.example.getdoc.ui.patient.component.PatientBottomBarComponent
import com.example.getdoc.ui.patient.state.PatientHomeUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHomeScreen(
    state: PatientHomeUiState,
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onDoctorsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearch: (String) -> Unit
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
            UserProfileHeader(name = state.name, location = state.location)



            Search(query = state.searchQuery,  onQueryChange = onSearch)
            SpecialtiesSection()
            TopDoctorsSection(doctors = state.doctors)
        }
    }
}

@Composable
fun Search(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(text = "Search...") },
        placeholder = { Text(text = "Eg: 'MIMS'") },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    )
}


@Composable
fun UserProfileHeader(name: String, location: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = com.example.getdoc.R.drawable.hasan), // Placeholder image
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = "Hi, $name", fontSize = 20.sp, style = MaterialTheme.typography.headlineLarge)
            Text(text = "Your location: $location", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SpecialtiesSection() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Top Specialties", style = MaterialTheme.typography.headlineSmall)
            Text(text = "View All", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) {
                Card(
                    modifier = Modifier
                        .size(80.dp)
                        .weight(1f),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = com.example.getdoc.R.drawable.ic_launcher_foreground), // Placeholder image
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(text = "Specialty", fontSize = 12.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) {
                Card(
                    modifier = Modifier
                        .size(80.dp)
                        .weight(1f),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = com.example.getdoc.R.drawable.ic_launcher_foreground), // Placeholder image
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(text = "Specialty", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TopDoctorsSection(doctors: List<DoctorInfo>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Top Doctors", style = MaterialTheme.typography.headlineSmall)
            Text(text = "View All", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(doctors) { DoctorInfo ->
                DoctorCard(
                    name = DoctorInfo.name,
                    specialty = DoctorInfo.specialization,
                    experience = DoctorInfo.experience,
                    fee = DoctorInfo.consultingFee,
                    doctorImage = DoctorInfo.profileImage,
                    rating = DoctorInfo.rating

                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DoctorListingPagePreview() {
    val mockState = PatientHomeUiState(
        name = "John Doe",
        location = "New York",
        doctors = listOf(
            DoctorInfo(name = "Dr. Priya Hasan", specialization = "Cardiologist", experience =  5, consultingFee = 500),
            DoctorInfo(name = "Dr. Anil Kumar", specialization = "Dermatologist", experience = 10,  consultingFee = 500),
            DoctorInfo(name = "Dr. Karim Ahmed", specialization = "Pediatrician", experience = 8,  consultingFee = 500)
        )
    )

    PatientHomeScreen(
        state = mockState,
        onHomeClick = {},
        onAppointmentsClick = {},
        onDoctorsClick = {},
        onProfileClick = {},
        onSearch = {}
    )
}







