package com.example.getdoc.ui.patient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.getdoc.ui.patient.component.DoctorCard
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client

@Composable
fun AllDoctorsScreen(
    //state: PatientHomeUiState,
    onHomeClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onDoctorsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearch: (String) -> Unit,
    firestore: FirebaseFirestore,
    client: Client,
    navController: NavController
) {
    Scaffold(
        topBar = {
            HeaderSection2()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            val viewModel = remember { PatientViewModel(client, firestore) }
            val doctorList by viewModel.doctorList.collectAsState()


            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(doctorList) { doctor ->
                    DoctorCard(
                        doctor = doctor,
                        storage = io.appwrite.services.Storage(client),
                        bucketId = "your-bucket-id",
                        navController = navController // ✅ Pass NavController properly
                    )
                }


            }

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSection2() {
    TopAppBar(
        title = {
            Text(text = "All Doctors")
        }
    )
}








