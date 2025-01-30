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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.getdoc.R
import com.example.getdoc.navigation.AllDoctorsScreen
import com.example.getdoc.navigation.PatientProfileScreen
import com.example.getdoc.ui.doctor.profile.DoctorBottomNavigationItem
enum class PatientBottomNavigationItem {
    Home,
    Appointments,
    Doctors,
    Profile
}

@Composable
fun PatientBottomNavigationBar(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onDoctorsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var selectedNavigationItem by remember {
        mutableStateOf(PatientBottomNavigationItem.Home)
    }
    NavigationBar(
        modifier = modifier
    ) {
        NavigationBarItem(
            selected = selectedNavigationItem == PatientBottomNavigationItem.Home,
            onClick = {
                selectedNavigationItem = PatientBottomNavigationItem.Home
                onHomeClick()
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.round_home_24),
                    contentDescription = "Home",
                    modifier = Modifier.size(25.dp)
                )
            },
            label = { Text("Home",
                fontSize = 9.sp) }
        )
        NavigationBarItem(
            selected = selectedNavigationItem == PatientBottomNavigationItem.Appointments,
            onClick = {
                selectedNavigationItem = PatientBottomNavigationItem.Appointments
                onAppointmentsClick()
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.round_calendar_month_24),
                    contentDescription = "appointment",
                    modifier = Modifier.size(25.dp)
                )
            },
            label = { Text(
                "My Appointments",
                fontSize = 9.sp
            ) }
        )
        NavigationBarItem(
            selected = selectedNavigationItem == PatientBottomNavigationItem.Doctors,
            onClick = {
                selectedNavigationItem = PatientBottomNavigationItem.Doctors
                onDoctorsClick()
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.doctors),
                    contentDescription = "Doctor",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = { Text("Doctors",
                fontSize = 9.sp)}
        )
        NavigationBarItem(
            selected = selectedNavigationItem == PatientBottomNavigationItem.Profile,
            onClick = {
                selectedNavigationItem = PatientBottomNavigationItem.Profile
                onProfileClick()
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.round_person_24),
                    contentDescription = "profile",
                    modifier = Modifier.size(25.dp)
                )
            },
            label = { Text("Profile",
                fontSize = 9.sp) }
        )
    }
}

@Composable
fun PatientBottomBarComponent(
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onDoctorsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
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
                onClick = { navController.navigate(AllDoctorsScreen)}
            )
            BottomBarItem(
                iconResId = R.drawable.img_10,
                label = "Profile",
                onClick = { navController.navigate(PatientProfileScreen) }
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