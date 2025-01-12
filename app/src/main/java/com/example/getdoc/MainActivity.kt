package com.example.getdoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.getdoc.navigation.AppNavigation

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)  // Initialize Firebase
          // Get FirebaseAuth instance
        FirebaseFirestore.getInstance()  // Get FirebaseFirestore instance

        setContent {
             AppNavigation()

        }
    }
}


