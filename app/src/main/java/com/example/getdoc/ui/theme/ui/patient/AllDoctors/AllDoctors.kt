package com.example.getdoc.ui.theme.ui.patient.AllDoctors

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.ui.theme.ui.patient.Appointments.BottomAppBar
import com.example.getdoc.ui.theme.ui.patient.Appointments.HeaderSection
import com.example.getdoc.ui.theme.ui.patient.SearchBar
import com.example.getdoc.ui.theme.ui.patient.SpecialtiesSection
import com.example.getdoc.ui.theme.ui.patient.TopDoctorsSection
import com.example.getdoc.ui.theme.ui.patient.UserProfileHeader

@Composable
fun AllDoctors() {
    Scaffold(
        bottomBar = { BottomAppBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            HeaderSection2()
            Spacer(modifier = Modifier.height(16.dp))
            SearchBar()
            TopDoctorsSection2()
        }
    }
}



@Composable
fun HeaderSection2() {
    TopAppBar(
        title = { Text(text = "All Doctors") },
        backgroundColor = MaterialTheme.colors.primary
    )
}


@Composable
fun TopDoctorsSection2() {
    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items((1..2).toList()) {
                DoctorCard(name = "Dr Priya Hasan", specialty = "Cardiologist", experience = "4 years", fee = "500", doctorImage = com.example.getdoc.R.drawable.doc2)
                DoctorCard(name = "Dr Anil", specialty = "Cardiologist", experience = "4 years", fee = "600", doctorImage = com.example.getdoc.R.drawable.doc1)
                DoctorCard(name = "Dr Karim", specialty = "Cardiologist", experience = "4 years", fee = "700", doctorImage = com.example.getdoc.R.drawable.doc1)
                DoctorCard(name = "Dr David", specialty = "Cardiologist", experience = "4 years", fee = "700", doctorImage = com.example.getdoc.R.drawable.doc1)
            }
        }
    }
}

@Composable
fun DoctorCard(name: String, specialty: String, experience: String, fee: String, doctorImage: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = doctorImage), // Placeholder image
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                androidx.compose.material3.Text(text = name, style = androidx.compose.material.MaterialTheme.typography.h6)
                androidx.compose.material3.Text(text = specialty, fontSize = 14.sp, color = Color.Gray)
                androidx.compose.material3.Text(text = "$experience Experience", fontSize = 14.sp, color = Color.Gray)
            }

            Column(
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(

                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.width(30.dp))
                    Image(
                        painter = painterResource(id = com.example.getdoc.R.drawable.star),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp))
                }


                Spacer(modifier = Modifier.height(30.dp))

                Row(

                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.width(30.dp))
                    androidx.compose.material3.Text(text = "৳$fee", style = androidx.compose.material.MaterialTheme.typography.h6, color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                }


            }

        }
    }
}





@Preview
@Composable
private fun AllDoctorsPrev() {
    AllDoctors()
}