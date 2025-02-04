package com.example.getdoc.ui.doctor.profile

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.R
import com.example.getdoc.ui.doctor.DoctorViewModel
import com.example.getdoc.ui.doctor.appointments.HeaderComponent
import kotlinx.coroutines.launch

@Composable
fun MyCredentialsPageComponent(
    viewModel: DoctorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var licenseUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // **Fetch the latest doctor status from Firestore**
    LaunchedEffect(uiState.id) {
        uiState.id?.let {
            viewModel.fetchDoctorStatus(it)
        }
        Log.d("MyCredentialsPage", "UI Updated: Status -> ${uiState.status}")
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> licenseUri = uri }

    Column(
        modifier = modifier.fillMaxSize().background(Color(0xFFF8F9FA)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderComponent(title = "My Credentials", iconResId = R.drawable.img_9)

        // **Submission Status Card**
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(
                when (uiState.status) {
                    "approved" -> Color(0xFFE0F2F1)  // Greenish for approved
                    "pending" -> Color(0xFFFFF3E0)   // Yellowish for pending
                    "declined" -> Color(0xFFFFEBEE)  // Reddish for declined
                    else -> Color(0xFFF5F5F5)
                }
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Submission Status: ${uiState.status?.uppercase()}",
                    fontSize = 18.sp,
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )

                if (uiState.status == "declined" && !uiState.rejectionReason.isNullOrEmpty()) {
                    Text(
                        text = "Reason: Admin declined your request.",
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { FormFieldComponent("Full Name", uiState.name ?: "", { viewModel.updateUiState("name", it) }) }
            item { FormFieldComponent("Degree", uiState.degree ?: "", { viewModel.updateUiState("degree", it) }) }
            item { FormFieldComponent("Speciality", uiState.speciality ?: "", { viewModel.updateUiState("speciality", it) }) }
            item { FormFieldComponent("DOB", uiState.dob ?: "", { viewModel.updateUiState("dob", it) }) }
            item { FormFieldComponent("Clinic Address", uiState.address ?: "", { viewModel.updateUiState("address", it) }) }
            item { FormFieldComponent("Consultation Fee", uiState.fee ?: "", { viewModel.updateUiState("fee", it) }) }
            item { FormFieldComponent("About You", uiState.aboutYou ?: "", { viewModel.updateUiState("aboutYou", it) }) }
        }

        // **Only show actions if NOT DECLINED**
        if (uiState.status != "declined") {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.status == "approved") {
                    Text(
                        text = "Please fill all the fields before updating your profile.",
                        fontSize = 14.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.updateDoctorProfile(context)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Blue)
                    ) {
                        Text("Update Profile")
                    }
                } else {
                    Button(
                        onClick = { filePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) {
                        Text("Upload License")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(Color.Red)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = {
                                if (licenseUri == null) {
                                    Toast.makeText(context, "Please select a license file!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.submitDoctorCredentialProfile(context, licenseUri!!)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(Color.Blue)
                        ) {
                            Text(if (uiState.status == "declined") "Resubmit" else "Submit")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormFieldComponent(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Text(text = label, fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                singleLine = true,
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp)
            )
        }
    }
}
