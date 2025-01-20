package com.example.getdoc.ui.patient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.ui.patient.component.PatientBottomBarComponent
import com.example.getdoc.ui.patient.state.PatientHomeUiState

@Composable
fun AllDoctorsScreen(
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

            HeaderSection2()
            Spacer(modifier = Modifier.height(16.dp))
            Search(query = state.searchQuery,  onQueryChange = onSearch)
            TopDoctorsSection(doctors = state.doctors)

        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSection2() {
    TopAppBar(
        title = {
            Text(text = "All Doctors")
        }
    )
}










@Preview(showBackground = true)
@Composable
fun AllDoctorsPreview() {
    val mockState = PatientHomeUiState(
        name = "John Doe",
        location = "New York",
        searchQuery = "",
        doctors = listOf(
            DoctorInfo(name = "Dr. Priya Hasan", specialization = "Cardiologist", experience =  5, consultingFee = 500),
            DoctorInfo(name = "Dr. Anil Kumar", specialization = "Dermatologist", experience = 10,  consultingFee = 500),
            DoctorInfo(name = "Dr. Karim Ahmed", specialization = "Pediatrician", experience = 8,  consultingFee = 500)
        )
    )

    AllDoctorsScreen(
        state = mockState,
        onHomeClick = { /* No-op for preview */ },
        onAppointmentsClick = { /* No-op for preview */ },
        onDoctorsClick = { /* No-op for preview */ },
        onProfileClick = { /* No-op for preview */ },
        onSearch = { /* Handle search query */ }
    )
}

