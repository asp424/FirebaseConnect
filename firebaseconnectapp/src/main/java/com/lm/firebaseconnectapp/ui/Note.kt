package com.lm.firebaseconnectapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lm.firebaseconnectapp.firebaseChat

@Composable
fun Note(
    modifier: Modifier, notesText: String, i: Int, navController: NavHostController
) {
    Card(
        modifier = modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                firebaseChat.setChatId(i)
                navController.navigate("chat")
            },
        shape = RoundedCornerShape(8.dp), border = BorderStroke(2.dp, Color.Blue)
    ) {
        Box(Modifier.padding(10.dp), CenterStart) {
            Column {
                Text(
                    text = notesText, maxLines = 1,
                    fontSize = 12.sp, color = Color.Gray
                )
                if (notesText.isNotEmpty()) {
                    Text(
                        text = notesText, maxLines = 1,
                        fontSize = 12.sp, color = Color.Gray
                    )
                }
                Text(
                    text = notesText, maxLines = 1,
                    fontSize = 12.sp, color = Color.Gray
                )
            }
        }
    }
}
