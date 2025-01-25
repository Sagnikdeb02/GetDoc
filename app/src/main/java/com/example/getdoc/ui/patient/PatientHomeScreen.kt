package com.example.getdoc.ui.patient

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.getdoc.R
import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.navigation.PatientHomeScreen
import com.example.getdoc.navigation.PatientProfileInputScreen
import com.example.getdoc.ui.patient.component.PatientBottomBarComponent
import com.example.getdoc.ui.patient.state.PatientHomeUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHomeScreen(
    homeState: PatientHomeUiState,
    viewModel: PatientViewModel,
    firestore: FirebaseFirestore,
    client: Client,
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onDoctorsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearch: (String) -> Unit,
    navController: NavHostController
) {
    Scaffold(
        topBar ={
            PatientTopBar(
                viewModel = viewModel,
                userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            )


        },

        bottomBar = {
            PatientBottomBarComponent(
                onHomeClick = { navController.navigate(PatientHomeScreen) },
                onAppointmentsClick = { navController.navigate("") },
                onDoctorsClick = { navController.navigate("doctorsScreen") },
                onProfileClick = { navController.navigate(PatientProfileInputScreen) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Search(query = homeState.searchQuery,  onQueryChange = onSearch)
            SpecialtiesSection()
            TopDoctorsSection(
                viewModel,
                client = client
            )
        }
    }
}

@Composable
fun Search(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(text = "Search...") },
        placeholder = { Text(text = "Eg: 'MIMS'") },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    )
}




@Composable
fun SpecialtiesSection() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Top Specialties", style = MaterialTheme.typography.headlineSmall)
            Text(text = "View All", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) {
                Card(
                    modifier = Modifier
                        .size(80.dp)
                        .weight(1f),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder image
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(text = "Specialty", fontSize = 12.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

                Card(
                    modifier = Modifier
                        .size(80.dp)
                        .weight(1f),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder image
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(text = "Specialty", fontSize = 12.sp)
                    }
                }
            }
        }
    }

@Composable
fun TopDoctorsSection(viewModel: PatientViewModel, client: Client) {
    val doctors by viewModel.doctorList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val storage = Storage(client)

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
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
            Text("No doctors found", color = Color.Red)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(doctors) { doctor ->
                    DoctorCard(
                        doctor = doctor,
                        storage = storage,
                        bucketId = "678e94b20023a8f92be0"
                    )
                }
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: DoctorInfo, storage: Storage, bucketId: String) {
    var imageData by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(doctor.profileImage) {
        isLoading = true
        try {
            val imageId = doctor.profileImage.trim()
            if (imageId.isNotEmpty() && imageId.all { it.isDigit() }) {
                val result = withContext(Dispatchers.IO) {
                    storage.getFileDownload(bucketId = bucketId, fileId = imageId)
                }
                imageData = result
            } else {
                Log.e("ImageFetch", "Invalid image ID: ${doctor.profileImage}")
            }
        } catch (e: Exception) {
            Log.e("DoctorCard", "Error loading image: ${e.localizedMessage}")
        } finally {
            isLoading = false
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(60.dp))
            } else {
                if (imageData != null) {
                    Image(
                        bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData!!.size).asImageBitmap(),
                        contentDescription = "Doctor Image",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.doctors),
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = "Dr ${doctor.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = doctor.specialization, fontSize = 14.sp, color = Color.Gray)
                Text(text = doctor.location, fontSize = 12.sp, color = Color.Gray)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painterResource(id = R.drawable.star),
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700)
                    )
                    Text(text = "${doctor.rating} Rating", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Consulting Fee",
                        color = Color.Blue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "৳ ${doctor.consultingFee}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
