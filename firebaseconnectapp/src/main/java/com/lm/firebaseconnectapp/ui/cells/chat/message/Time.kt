package com.lm.firebaseconnectapp.ui.cells.chat.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep

@Composable
fun List<Pair<String, String>>.Time(i: Int) {
    with(mainDep.firebaseConnect) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(
                    bottom = getTimePadding(i, this@with, 15.dp),
                    top = getTimePadding(i, this@with, 5.dp)
                ),
            contentAlignment = if (get(i).second == "green") CenterEnd else CenterStart,
        ) {
            Text(
                getTimeText(i, this@with),
                fontSize = 10.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

private fun List<Pair<String, String>>.getTimeText(i: Int, firebaseConnect: FirebaseConnect) =
    with(firebaseConnect.firebaseRead) {
        val current = get(i).first.getTime()
        if (i != 0 && i != lastIndex) {
            if (get(i + 1).second == get(i).second && get(i + 1).first.getTime() == current) ""
            else current
        } else current
    }

private fun List<Pair<String, String>>.getTimePadding(
    i: Int, firebaseConnect: FirebaseConnect, padding: Dp
) = with(firebaseConnect.firebaseRead) {
    if (i != 0 && i != lastIndex) {
        if (get(i + 1).second == get(i).second &&
            get(i + 1).first.getTime() == get(i).first.getTime()
        ) 0.dp
        else padding
    } else padding
}