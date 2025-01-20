package com.example.getdoc.ui.patient.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.getdoc.R
import com.example.getdoc.ui.doctor.appointments.BottomBarComponent


@Composable
fun PatientBottomBarComponent(
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onDoctorsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            BottomBarItem(
                iconResId = R.drawable.img_6,
                label = "Home",
                onClick = onHomeClick
            )
            BottomBarItem(
                iconResId = R.drawable.img_8,
                label = "My Appointments",
                onClick = onAppointmentsClick
            )
            BottomBarItem(
                iconResId = R.drawable.doctors,
                label = "All Doctors",
                onClick = onDoctorsClick
            )
            BottomBarItem(
                iconResId = R.drawable.img_10,
                label = "Profile",
                onClick = onProfileClick
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

}

@Composable
fun BottomBarItem(
    iconResId: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier.size(25.dp)
        )
        Text(text = label)
    }
}

@Preview
@Composable
private fun bot() {
    PatientBottomBarComponent(
        onHomeClick = {},
        onAppointmentsClick = {},
        onDoctorsClick = {},
        onProfileClick = {}
    )
}