package com.example.getdoc.ui.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.getdoc.R


@Composable
fun ChooseRoleScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Registration",
                fontSize = 26.sp, color =
                Color(0xFF174666)
            )
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.doctor_logo),
                    contentDescription = "",
                    modifier = Modifier
                        .size(height = 190.dp, width = 170.dp)
                        .clip(RoundedCornerShape(20.dp)),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.patient_logo),
                    contentDescription = "",
                    modifier = Modifier
                        .size(height = 190.dp, width = 170.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.size(height = 50.dp, width = 120.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF174666)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Doctor",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.size(height = 50.dp, width = 120.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF174666)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Patient",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "already have an account", color = Color.Gray)
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "Login", color = Color(0xFF174666))
                }
            }
        }
    }
}