package com.example.getdoc.ui.doctor

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.getdoc.R
import com.example.getdoc.navigation.DoctorCredentialsScreen
import com.example.getdoc.navigation.DoctorProfileInputScreen
import com.example.getdoc.navigation.DoctorProfileScreen
import com.example.getdoc.ui.doctor.profile.BottomBarComponent
import com.example.getdoc.ui.doctor.profile.MyCredentialsPageComponent
import com.example.getdoc.ui.doctor.profile.UploadDoctorProfilePictureScreen
import com.google.firebase.firestore.FirebaseFirestore
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorHomeScreen(
    viewModel: DoctorViewModel,
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    navController: NavHostController
) {

//    MyCredentialsPageComponent(
//        viewModel = viewModel,
//        onHomeClick,
//        onAppointmentsClick,
//        onProfileClick,
//        modifier = Modifier
//    )




    var calendarShow by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val dayOfWeek =
                        Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.of("UTC")).dayOfWeek
                    dayOfWeek != DayOfWeek.SUNDAY && dayOfWeek != DayOfWeek.SATURDAY
                } else {
                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    calendar.timeInMillis = utcTimeMillis
                    calendar[Calendar.DAY_OF_WEEK] != Calendar.SUNDAY &&
                            calendar[Calendar.DAY_OF_WEEK] != Calendar.SATURDAY
                }
            }

            override fun isSelectableYear(year: Int): Boolean = year > 2022
        }
    )

    val patients = listOf(
        Patient("Priya Hasan", "Female", 42, "100068"),
        Patient("Rahul Dey", "Male", 35, "100101"),
        Patient("Anita Roy", "Female", 29, "100152"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F7))
            .padding(16.dp)
    ) {
        // Header
        Header()

        // Search Field
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Eg: \"MIMS\"") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.img_13),
                    contentDescription = "Search Icon"
                )
            },
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar and Patients
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Patients (${patients.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Icon(
                painter = painterResource(id = R.drawable.img_14),
                contentDescription = "Calendar Icon",
                modifier = Modifier
                    .clickable { calendarShow = !calendarShow }
                    .size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (calendarShow) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                val height = maxHeight * 0.7f
                Box(modifier = Modifier.height(height)) {
                    DatePicker(state = datePickerState)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Patient List
        LazyColumn {
            items(patients) { patient ->
                PatientCard(patient)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            BottomBarComponent(
                onHomeClick = onHomeClick,
                onAppointmentsClick = onAppointmentsClick,
                 onProfileClick = onProfileClick





            )
        }
    }
}

@Composable
fun PatientCard(patient: Patient) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(patient.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(patient.gender, fontSize = 14.sp, color = Color(0xFF2E7D32))
            Text("${patient.age} Years", fontSize = 14.sp, color = Color.Gray)
            Text(patient.id, fontSize = 14.sp, color = Color.Gray)
        }
        Button(
            onClick = { /* Decline Action */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Decline", color = Color.White)
        }
    }
}


@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.img_7),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Hi, Christopher",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Your Location: Sylhet",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.img_12),
            contentDescription = "Location Icon",
            modifier = Modifier.size(24.dp),
            tint = Color.Black
        )
    }
}


data class Patient(val name: String, val gender: String, val age: Int, val id: String)
