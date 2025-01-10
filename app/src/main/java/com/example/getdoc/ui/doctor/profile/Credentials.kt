package com.example.getdoc.ui.doctor.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.getdoc.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp

@Composable
fun MyCredentialsPage() {
    // States for the form fields
    val degree = remember { mutableStateOf(TextFieldValue("")) }
    val speciality = remember { mutableStateOf(TextFieldValue("")) }
    val dob = remember { mutableStateOf(TextFieldValue("")) }
    val address = remember { mutableStateOf(TextFieldValue("")) }
    val fee = remember { mutableStateOf(TextFieldValue("")) }
    val aboutYou = remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F3F5)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(0xFF1565C0))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "My Credentials",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )
            Image(painter = painterResource(id = R.drawable.img_9), contentDescription = "")
        }

        // Form fields
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            FormField(label = "Enter Your Degree", value = degree.value, onValueChange = { degree.value = it })
            FormField(label = "Enter Your Speciality", value = speciality.value, onValueChange = { speciality.value = it })
            FormField(label = "Enter Your DOB", value = dob.value, onValueChange = { dob.value = it })
            FormField(label = "Enter Your Clinic Address", value = address.value, onValueChange = { address.value = it })
            FormField(label = "Enter Your Consultation Fee", value = fee.value, onValueChange = { fee.value = it })
            FormField(label = "About You", value = aboutYou.value, onValueChange = { aboutYou.value = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* Cancel action */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(Color(0xFF174666))
                ) {
                    Text(text = "Cancel")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { /* Submit action */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(Color(0xFF174666))
                ) {
                    Text(text = "Submit")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            BottomBar()
        }
    }
}

@Composable
fun FormField(label: String, value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    BorderStroke(width = 1.dp, color = Color.Gray),
                    shape = RoundedCornerShape(20.dp)
                )
                .background(Color.White)
                .padding(8.dp)
                .height(40.dp)


        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMyCredentialsPage() {
    MyCredentialsPage()
}



@Composable
fun BottomBar(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.img_6),
                contentDescription = "",
                modifier = Modifier.size(25.dp)
            )
            Text(text = "Home")
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.img_8),
                contentDescription = "",
                modifier = Modifier.size(25.dp)
            )
            Text(text = "My Appointments")
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.img_10),
                contentDescription = "",
                modifier = Modifier.size(25.dp)
            )
            Text(text = "Profile")
        }
    }
}