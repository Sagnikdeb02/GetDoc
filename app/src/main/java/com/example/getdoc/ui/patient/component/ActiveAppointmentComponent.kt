package com.example.getdoc.ui.patient.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.R
import com.example.getdoc.data.model.AppointmentInfo


@Composable
fun ActiveAppointmentComponent(
    appointment: AppointmentInfo,
    onRescheduleClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder image
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = appointment.doctorName, style = MaterialTheme.typography.headlineSmall)
                    Text(text = "(${appointment.specialty})", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "Date: ${appointment.date}", fontSize = 14.sp)
                    Text(
                        text = appointment.daysCount,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Time/Token: ${appointment.time}", fontSize = 14.sp, color = Color.Gray)
            Text(text = "Place: ${appointment.place}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRescheduleClick
                ) {
                    Text(text = "Reschedule", color = Color.White)
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onCancelClick
                ) {
                    Text(text = "Cancel", color = Color.White)
                }
            }
        }
    }
}