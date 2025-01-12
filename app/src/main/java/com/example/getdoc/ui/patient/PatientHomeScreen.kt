package com.example.getdoc.ui.patient

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DoctorListingPage() {
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
            UserProfileHeader(name = "Hasan")
            SearchBar(
                searchQuery = "",
                onSearch = {}
            )
            SpecialtiesSection()
            TopDoctorsSection()
        }
    }
}

@Composable
fun UserProfileHeader(name: String) {
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
            Text(text = "Your location: Sylhet", fontSize = 14.sp, color = Color.Gray)
        }
    }
}


@Composable
fun SearchBar(
    searchQuery: String,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit
) {

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearch,
        placeholder = { Text(text = "Eg: 'MIMS'") },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        singleLine = true
    )
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
fun TopDoctorsSection() {
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
            items((1..2).toList()) {
                DoctorCard(name = "Dr Priya Hasan", specialty = "Cardiologist", experience = "4 years", fee = "500", doctorImage = com.example.getdoc.R.drawable.doc2)
                DoctorCard(name = "Dr Anil", specialty = "Cardiologist", experience = "4 years", fee = "600", doctorImage = com.example.getdoc.R.drawable.doc1)
                DoctorCard(name = "Dr Karim", specialty = "Cardiologist", experience = "4 years", fee = "600", doctorImage = com.example.getdoc.R.drawable.doc1)
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
                Text(text = name, style = MaterialTheme.typography.headlineSmall)
                Text(text = specialty, fontSize = 14.sp, color = Color.Gray)
                Text(text = "$experience Experience", fontSize = 14.sp, color = Color.Gray)
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
                    Text(text = "৳$fee", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                }


            }

        }
    }
}




@Preview
@Composable
private fun DoctorListingPageprev() {
    DoctorListingPage()
}
