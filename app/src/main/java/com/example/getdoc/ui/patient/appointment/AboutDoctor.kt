package com.example.getdoc.ui.patient.appointment

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.ui.patient.DoctorCard
import com.example.getdoc.ui.patient.component.CustomAppBar
import com.example.getdoc.ui.patient.component.ReviewItem

@Composable
fun DoctorDetailsScreen(doctor: DoctorInfo, onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            CustomAppBar(
                title = doctor.name,
                onBackClick = onBackClick
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Profile Section
//                DoctorCard(
//                    name = doctor.name,
//                    specialty = doctor.specialization,
//                    experience = doctor.experience,
//                    fee = doctor.consultingFee,
//                    doctorImage = doctor.profileImage,
//                    rating = doctor.rating,
//                    client = doctor.nam
//                )
                Spacer(modifier = Modifier.height(16.dp))


                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                )  {
                    Button(
                        onClick = { /* TODO: Handle Book Now */ },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text(text = "Book Now")
                    }
                    Button(
                        onClick = { /* TODO: Handle Call */ },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text(text = "Call")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // About Doctor Section
                Text(
                    text = "About Doctor",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = doctor.about,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Reviews Section
                Text(
                    text = "Reviews (23)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                repeat(2) {
                    ReviewItem(
                        name = "Sam",
                        date = "12/12/2023",
                        review = doctor.about
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                }}
        }
    )

}


