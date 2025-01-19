package com.example.getdoc.ui.doctor.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.getdoc.R
import com.example.getdoc.ui.doctor.appointments.BottomBarComponent

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


@Composable
fun BottomBarComponent(
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            iconResId = R.drawable.img_10,
            label = "Profile",
            onClick = onProfileClick
        )
    }
}


@Composable
fun BottomBarScreen() {
    BottomBarComponent(
        onHomeClick = { /* Navigate to Home */ },
        onAppointmentsClick = { /* Navigate to My Appointments */ },
        onProfileClick = { /* Navigate to Profile */ }
    )
}