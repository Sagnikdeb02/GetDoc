package com.example.getdoc.ui.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.getdoc.ui.patient.component.DoctorCard
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllDoctorsScreen(
    viewModel: PatientViewModel,
    firestore: FirebaseFirestore,
    client: Client,
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    val doctors by viewModel.doctorList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val filteredDoctors = doctors.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "All Doctors",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Search(query = searchQuery, onQueryChange = { searchQuery = it })
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(filteredDoctors) { doctor ->
                        DoctorCard(
                            doctor = doctor,
                            bucketId = "678dd5d30039f0a22428",
                            navController = navController,
                            client = client,
                            firestore = firestore,
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun Search(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(text = "Search...") },
        placeholder = { Text(text = "Eg: 'Dr. John'") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(40.dp)
    )
}
