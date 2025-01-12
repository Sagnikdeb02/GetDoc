package com.example.getdoc.ui.authentication.doctor_registration

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun AdminApprovalScreen(viewModel: AdminViewModel) {
    val doctors = viewModel.unapprovedDoctors.collectAsState(initial = emptyList())

    LazyColumn {
        items(doctors.value) { doctor ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(doctor.username)
                    Text(doctor.email)
                }
                Row {
                    Button(onClick = { viewModel.approveDoctor(doctor.id) }) {
                        Text("Approve")
                    }
                    Button(onClick = { viewModel.rejectDoctor(doctor.id) }) {
                        Text("Reject")
                    }
                }
            }
        }
    }
}
