package com.example.getdoc.ui.doctor.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.getdoc.R
import com.example.getdoc.navigation.DoctorCredentialsScreen
import com.example.getdoc.navigation.DoctorProfileScreen
import com.example.getdoc.ui.doctor.appointments.BottomBarComponent

enum class DoctorBottomNavigationItem {
    Home,
    Appointments,
    Profile
}


@Composable
fun DoctorBottomNavigationBar(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var selectedNavigationItem by remember {
        mutableStateOf(DoctorBottomNavigationItem.Home)
    }
    NavigationBar(
        modifier = modifier
    ) {
        NavigationBarItem(
            selected = selectedNavigationItem == DoctorBottomNavigationItem.Home,
            onClick = {
                selectedNavigationItem = DoctorBottomNavigationItem.Home
                onHomeClick()
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.round_home_24),
                    contentDescription = "Home",
                    modifier = Modifier.size(25.dp)
                )
            },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = selectedNavigationItem == DoctorBottomNavigationItem.Appointments,
            onClick = {
                selectedNavigationItem = DoctorBottomNavigationItem.Appointments
                onAppointmentsClick()
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.round_calendar_month_24),
                    contentDescription = "Home",
                    modifier = Modifier.size(25.dp)
                )
            },
            label = { Text("Appointments") }
        )
        NavigationBarItem(
            selected = selectedNavigationItem == DoctorBottomNavigationItem.Profile,
            onClick = {
                selectedNavigationItem = DoctorBottomNavigationItem.Profile
                onProfileClick()
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.round_person_24),
                    contentDescription = "Home",
                    modifier = Modifier.size(25.dp)
                )
            },
            label = { Text("Profile") }
        )
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


@Composable
fun BottomBarComponent(
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
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
            onClick = { navController.navigate(DoctorCredentialsScreen) }//onAppointmentsClick
        )
        BottomBarItem(
            iconResId = R.drawable.img_10,
            label = "Profile",
            onClick = { navController.navigate(DoctorProfileScreen) }
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