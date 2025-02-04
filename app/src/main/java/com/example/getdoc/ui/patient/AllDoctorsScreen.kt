package com.example.getdoc.ui.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.getdoc.ui.patient.component.DoctorCard
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import io.appwrite.services.Storage

@Composable
fun AllDoctorsScreen(
    firestore: FirebaseFirestore,
    client: Client,
    navController: NavController
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp) // Added padding
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

            val viewModel = remember { PatientViewModel(client, firestore) }
            val doctorList by viewModel.doctorList.collectAsState()


            LazyColumn {
                items(doctorList) { doctor ->
                    DoctorCard(
                        doctor = doctor,
                        bucketId = "678dd5d30039f0a22428",
                        navController = navController,
                        client = client,
                        firestore = firestore,
                    )
                }

                // Add extra space at the bottom to prevent shadow cutoff
                item {
                    Spacer(modifier = Modifier.height(40.dp))
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








