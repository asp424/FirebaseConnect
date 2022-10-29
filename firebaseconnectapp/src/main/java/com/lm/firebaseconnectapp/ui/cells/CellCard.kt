package com.lm.firebaseconnectapp.ui.cells

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lm.firebaseconnectapp.firebaseConnect

@Composable
fun CellCard(
    navController: NavHostController,
    id: String,
    inCard: @Composable () -> Unit) {
    val haptic = LocalHapticFeedback.current
    ElevatedCard(shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                firebaseConnect.setChatId(id.toInt())
                navController.navigate("chat")
            }
            .padding(start = 16.dp, end = 16.dp, top = 6.dp)) {
             inCard()
    }
}

