package com.example.getdoc.ui.patient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.getdoc.R

@Composable
fun AllDoctors() {
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

            HeaderSection2()
            Spacer(modifier = Modifier.height(16.dp))
//            SearchBar()
            TopDoctorsSection2()
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


@Composable
fun TopDoctorsSection2() {
    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items((1..2).toList()) {
                DoctorCard(name = "Dr Priya Hasan", specialty = "Cardiologist", experience = "4 years", fee = "500", doctorImage = R.drawable.doc2)
                DoctorCard(name = "Dr Anil", specialty = "Cardiologist", experience = "4 years", fee = "600", doctorImage = R.drawable.doc1)
                DoctorCard(name = "Dr Karim", specialty = "Cardiologist", experience = "4 years", fee = "700", doctorImage = R.drawable.doc1)
                DoctorCard(name = "Dr David", specialty = "Cardiologist", experience = "4 years", fee = "700", doctorImage = R.drawable.doc1)
            }
        }
    }
}

//@Composable
//fun DoctorCard(name: String, specialty: String, experience: String, fee: String, doctorImage: Int) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Row(modifier = Modifier.padding(16.dp)) {
//            Image(
//                painter = painterResource(id = doctorImage), // Placeholder image
//                contentDescription = null,
//                modifier = Modifier
//                    .size(60.dp)
//                    .clip(CircleShape)
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Column(modifier = Modifier.weight(1f)) {
//                Text(text = name, style = MaterialTheme.typography.headlineSmall)
//                Text(text = specialty, fontSize = 14.sp, color = Color.Gray)
//                Text(text = "$experience Experience", fontSize = 14.sp, color = Color.Gray)
//            }
//
//            Column(
//                verticalArrangement = Arrangement.SpaceBetween
//            ) {
//                Row(
//
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Spacer(modifier = Modifier.width(30.dp))
//                    Image(
//                        painter = painterResource(id = R.drawable.star),
//                        contentDescription = "",
//                        modifier = Modifier.size(50.dp))
//                }
//
//
//                Spacer(modifier = Modifier.height(30.dp))
//
//                Row(
//
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Spacer(modifier = Modifier.width(30.dp))
//                    Text(text = "৳$fee", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
//                }
//
//
//            }
//
//        }
//    }
//}





@Preview
@Composable
private fun AllDoctorsPrev() {
    AllDoctors()
}