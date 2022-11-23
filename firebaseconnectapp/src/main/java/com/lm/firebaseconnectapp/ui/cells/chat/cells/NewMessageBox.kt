package com.lm.firebaseconnectapp.ui.cells.chat.cells

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getUnreadIndex

@Composable
fun NewMessageBox(index: Int) {

    if (getUnreadIndex == index) Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp)
            .background(UiStates.getMainColor.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Новые сообщения", fontWeight = FontWeight.Bold, color = Color.White,
            modifier = Modifier.padding(3.dp)
        )
    }
}