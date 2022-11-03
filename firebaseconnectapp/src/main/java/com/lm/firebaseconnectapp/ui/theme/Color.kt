package com.lm.firebaseconnectapp.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnectapp.R

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val main = Color(0xFF00BCD4)
val second = Color(0xFFFFFFFF)

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Teal200 = Color(0xFF03DAC5)
val Navy500 = Color(0xFF64869B)
val Navy500_1 = Color(0xFF6D93AA)
val Navy900 = Color(0xFF073042)
val Green300 = Color(0xFF3DDC84)
val Green900 = Color(0xFF00A956)
val darkGreen = Color(0xFF3AB33F)
val listColors = listOf(Green900, Color.Blue,
    Color.Magenta, Color.Red, Teal200, Navy900, Navy500, Navy500_1, Purple200, Purple500, Green300)

val H7 = TextStyle(
    shadow = Shadow(
        offset = Offset(30f, 15f), color = Color.Gray, blurRadius = 20f
    ),
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    letterSpacing = 0.15.sp,
    textAlign = TextAlign.Start,
    fontFamily = FontFamily(
        Font(R.font.merriweather_bold)
    )
)

val H8 = TextStyle(
    shadow = Shadow(
        offset = Offset(30f, 15f), color = Color.Gray, blurRadius = 20f
    ),
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    letterSpacing = 0.15.sp,
    textAlign = TextAlign.Start,
    fontFamily = FontFamily(
        Font(R.font.merriweathertalic)
    )
)