package com.example.getdoc.ui.doctor.profile


import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.getdoc.ui.doctor.DoctorViewModel
import com.google.firebase.auth.FirebaseAuth
import io.appwrite.Client
import kotlinx.coroutines.launch
@Composable
fun UploadDoctorProfilePictureScreen(
    viewModel: DoctorViewModel,
    modifier: Modifier = Modifier,
    client:Client
) {
      ProfileUpdateScreen(
          viewModel = viewModel,
          client = client,
          modifier = modifier
      )

}
