package com.example.getdoc.ui.patient.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.data.model.AppointmentInfo


@Composable
fun PreviousAppointmentComponent(
    appointment: AppointmentInfo,
    onAddReviewClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = appointment.daysCount, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = appointment.doctorName, style = MaterialTheme.typography.headlineSmall)
            Text(text = "(${appointment.specialty})", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Date: ${appointment.date}", fontSize = 14.sp)
            Text(text = "Time/Token: ${appointment.time}", fontSize = 14.sp)
            Text(text = "Place: ${appointment.place}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAddReviewClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text(text = "Add A Review", color = Color.White)
            }
        }
    }
}