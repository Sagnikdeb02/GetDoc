package com.example.getdoc.ui.theme.ui.patient.Appointments.BookingScreenElements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomButton(
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
    buttonColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(8.dp),
    fontSize: Int = 16
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        Text(text = buttonText, fontSize = fontSize.sp, color = textColor)
    }
}
