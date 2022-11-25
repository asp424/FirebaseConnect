package com.lm.firebaseconnectapp.ui.cells.chat.animations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.animDp
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getCurrentDateAtScroll
import com.lm.firebaseconnectapp.ui.UiStates.getDateCardVisible
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getVoiceBarVisible

@Composable
fun DateAnimation(
    visible: Boolean = getDateCardVisible,
    date: String = getCurrentDateAtScroll,
    paddingTop: Dp = if (getVoiceBarVisible) 35.dp else 10.dp,
    index: Int = 0
) {

    Box(
        Modifier
            .fillMaxSize()
            .padding(top = paddingTop, bottom = 10.dp)
            .offset(0.dp, animDp(getVoiceBarVisible && index == 0, 30.dp, 0.dp)),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            Modifier
                .scale(animScale(visible)),
            colors = CardDefaults.cardColors(
                containerColor = getMainColor.copy(0.3f)
            )
        ) {
            Text(
                text = date,
                Modifier
                    .padding(start = 15.dp, end = 15.dp, bottom = 2.dp, top = 2.dp)
                    .alpha(0.8f), color = Color.White, fontWeight = FontWeight.Bold
            )
        }
    }
}