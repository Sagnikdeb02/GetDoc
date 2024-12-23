package com.example.getdoc.ui.theme.ui.doctor.Appointments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.R
import com.example.getdoc.ui.theme.ui.doctor.Profile.BottomBar

@Composable
fun PatientDetailsPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F7)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(0xFF1565C0))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Patient Details",
                fontSize = 24.sp,
                color = Color.White
            )
            Image(painter = painterResource(id = R.drawable.img_9), contentDescription = "")
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(Color(0xFFA1DFD8), shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                PatientDetailRow(label = "Name:", value = "Sagnik Deb")
                PatientDetailRow(label = "Gender:", value = "Male")
                PatientDetailRow(label = "Age:", value = "32 Year Old")
                PatientDetailRow(label = "Booking For:", value = "Self")
                PatientDetailRow(label = "Booking ID:", value = "100068")
                PatientDetailRow(label = "Phone No:", value = "01711111111")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Patient type text
        Text(
            text = "Patient Type: Old Patient",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1565C0),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            BottomBar()
        }
    }
}

@Composable
fun PatientDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Black)
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewPatientDetailsPage() {
    PatientDetailsPage()
}
