package com.lm.firebaseconnectapp.ui.cells.chat.message

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor

@Composable
fun List<Pair<String, String>>.Message(i: Int) {

    Box(Modifier.fillMaxWidth(), if (get(i).second == "green") CenterEnd else CenterStart)
    {
        Box(
            Modifier
                .wrapContentWidth()
                .width(200.dp),
            if (get(i).second == "green") CenterEnd else CenterStart
        ) {
            Card(
                Modifier.wrapContentWidth(), getShape(i, get(i).second),
                border = BorderStroke(
                    1.dp, if (isSystemInDarkTheme()) White else getSecondColor
                )
            ) { get(i).first.TextMessageInbox() }
        }
    }
    Time(i)
}

private fun List<Pair<String, String>>.getShapeCorner(i: Int) =
    if (i != 0) {
        if (get(i - 1).second == get(i).second) {
            if (i != lastIndex) {
                if (get(i + 1).second == get(i).second) {
                    0.dp
                } else 10.dp
            } else 10.dp
        } else 10.dp
    } else 10.dp

private fun List<Pair<String, String>>.getShape(i: Int, side: String) =
    RoundedCornerShape(
        topStart = if (side == "green") 10.dp else 0.dp,
        bottomEnd = if (side == "green") 0.dp else 10.dp,
        bottomStart = if (side == "green") 10.dp else getShapeCorner(i),
        topEnd = if (side != "green") 10.dp else getShapeCorner(i)
    )
