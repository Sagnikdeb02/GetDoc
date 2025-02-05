package com.example.getdoc.ui.doctor.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun HelpScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Help & Support",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF008080)
        )

        // Issue Resolution Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)) // Light gray background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "If you experience issues, try restarting the app or checking for updates.",
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "For assistance, contact our Support Team:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Email: getdocsupport@gmail.com",
                    fontSize = 16.sp,
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        // Handle email click
                    }
                )
            }
        }

        // FAQs Section
        Text(
            text = "FAQs",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF008080)
        )

        FAQItem(question = "How do I cancel an appointment?", answer = "Go to the Appointments section, select the appointment, and choose Decline.")
        FAQItem(question = "How do I reset my password?", answer = "Go to Profile > Change Password.")
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Q: $question", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "A: $answer", fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HelpScreenPreview() {
    HelpScreen()
}
