package com.example.getdoc.ui.patient.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import com.example.getdoc.R
//
//@Composable
//fun DoctorCard(name: String, specialty: String, experience: Int, fee: Int, doctorImage: Comparable<*>, rating: Number = 0f) {
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
//                verticalArrangement = Arrangement.SpaceBetween,
//                horizontalAlignment = Alignment.End
//            ) {
//
//                Column (
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//
//                        Image(
//                            painter = painterResource(id = R.drawable.star2),
//                            contentDescription = "",
//                            modifier = Modifier.size(30.dp))
//
//
//                    Text(text = "$rating")
//                }
//
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
//
//@Preview(showBackground = true)
//@Composable
//fun DoctorCardPreview() {
//    DoctorCard(
//        name = "Dr. John Smith",
//        specialty = "Dermatologist",
//        experience = 10,
//        fee = 1500,
//        doctorImage = R.drawable.hasan, // Replace with your drawable resource
//        rating = 4.8f
//    )
