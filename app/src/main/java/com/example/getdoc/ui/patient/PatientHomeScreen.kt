package com.example.getdoc.ui.patient

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.getdoc.R
import com.example.getdoc.convertImageByteArrayToBitmap
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.fetchProfilePictureDynamically
import com.example.getdoc.fetchUsernameDynamically
import com.example.getdoc.theme.AppBackground
import com.example.getdoc.ui.patient.component.DoctorCard
import com.example.getdoc.ui.patient.state.PatientHomeUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHomeScreen(
    viewModel: PatientViewModel,
    firestore: FirebaseFirestore,
    client: Client,
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSpecialization by remember { mutableStateOf("All") }
    val specializations = listOf("All", "Cardiologist", "Neurologist", "Orthopedics", "ENT")
    val doctors by viewModel.doctorList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var username by remember { mutableStateOf("Loading...") }
    var profileImage by remember { mutableStateOf<ByteArray?>(null) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        username = fetchUsernameDynamically(firestore, userId)
        profileImage = fetchProfilePictureDynamically(client, firestore, userId)
    }

    val searchSuggestions = doctors.filter { it.name.contains(searchQuery, ignoreCase = true) }

    // **Fixed Filtering Logic**
    val displayedDoctors = remember(searchQuery, selectedSpecialization, doctors) {
        doctors.filter { doctor ->
            val specInDb = doctor.specialization.trim().lowercase()
            val selectedSpec = selectedSpecialization.trim().lowercase()
            println("Doctor: ${doctor.name}, Specialization in DB: $specInDb, Selected: $selectedSpec")

            val matchesSearch = searchQuery.isBlank() || doctor.name.contains(searchQuery, ignoreCase = true)
            val matchesSpecialization = selectedSpecialization == "All" || specInDb == selectedSpec
            matchesSearch && matchesSpecialization
        }
    }


    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GetDoc",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF008080),
                    modifier = Modifier.padding(16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(color = AppBackground).padding(16.dp)
                ) {
                    Text(text = "Hi, $username", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        profileImage?.let {
                            Image(
                                bitmap = convertImageByteArrayToBitmap(it).asImageBitmap(),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(40.dp).clip(CircleShape)
                            )
                        } ?: Icon(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Default Profile",
                            modifier = Modifier.size(40.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppBackground)
                .padding(top = paddingValues.calculateTopPadding() / 2)
        ) {
            Spacer(modifier = Modifier.height(25.dp))
            Search(query = searchQuery, onQueryChange = { searchQuery = it })
            if (searchQuery.isNotEmpty()) {
                DoctorSuggestions(searchSuggestions, navController, client, firestore)
            }
            FilterBar(selectedSpecialization, specializations) { selectedSpecialization = it }
            TopDoctorsSection(displayedDoctors, isLoading, navController, client, firestore)
        }
    }
}

@Composable
fun FilterBar(selected: String, specializations: List<String>, onFilterSelected: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        specializations.forEach { spec ->
            FilterChip(
                selected = selected == spec,
                onClick = { onFilterSelected(spec) },
                label = { Text(spec) }
            )
        }
    }
}


@Composable
fun DoctorSuggestions(suggestions: List<DoctorInfo>, navController: NavController, client: Client, firestore: FirebaseFirestore) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(suggestions) { doctor ->
            DoctorCard(
                doctor = doctor,
                bucketId = "678e94b20023a8f92be0",
                navController = navController,
                client = client,
                firestore = firestore
            )
        }
    }
}



@Composable
fun TopDoctorsSection(
    doctors: List<DoctorInfo>,
    isLoading: Boolean,
    navController: NavController,
    client: Client,
    firestore: FirebaseFirestore
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Top Doctors", style = MaterialTheme.typography.headlineSmall)
            Text(text = "View All", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (doctors.isEmpty()) {
            Text("No doctors found", color = Color.Red, modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(doctors) { doctor ->
                    DoctorCard(
                        doctor = doctor,
                        bucketId = "678e94b20023a8f92be0",
                        navController = navController,
                        client = client,
                        firestore = firestore
                    )
                }
            }
        }
    }
}
