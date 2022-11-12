package com.lm.firebaseconnectapp.ui.cells

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.record_sound.PlayerStates
import com.lm.firebaseconnectapp.record_sound.Recorder.Companion.IS_RECORD
import com.lm.firebaseconnectapp.showToast
import com.lm.firebaseconnectapp.ui.UiStates.getPlayerState
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor

@Composable
fun ChatMessage(listMessages: List<Pair<String, String>>, i: Int) {
    with(mainDep) {
        with(firebaseConnect) {
            val context = LocalContext.current
            listMessages[i].second.also { side ->
                listMessages[i].first.apply {
                    Box(
                        Modifier.fillMaxWidth(), contentAlignment =
                        if (side == "green") Alignment.CenterEnd
                        else Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .wrapContentWidth()
                                .width(200.dp),
                            contentAlignment =
                            if (side == "green") Alignment.CenterEnd
                            else Alignment.CenterStart
                        ) {
                            Card(
                                Modifier.wrapContentWidth(),
                                border = BorderStroke(
                                    1.dp, if (isSystemInDarkTheme()) White else getSecondColor
                                ),
                                shape = listMessages.getShape(i, side)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Center
                                ) {
                                    val text = removeMessageKey().trimStart()
                                    if (text.startsWith(IS_RECORD)) {
                                        Row(
                                            horizontalArrangement = Center,
                                            verticalAlignment = CenterVertically
                                        ) {
                                            FloatingActionButton(
                                                onClick = {
                                                    when (getPlayerState) {
                                                        PlayerStates.PLAYING -> {
                                                            recorder.pause()
                                                        }
                                                            PlayerStates.NULL -> {
                                                            firebaseStorage.readSound(
                                                                substringAfter(IS_RECORD)
                                                            ) {
                                                                if (it.isNotEmpty()) {
                                                                    recorder.create(it)
                                                                    recorder.play()
                                                                } else context.showToast("File not found")
                                                            }
                                                        }

                                                        PlayerStates.PAUSE -> {
                                                            recorder.play()
                                                        }
                                                    }
                                                }, modifier = Modifier
                                                    .padding(10.dp)
                                                    .size(40.dp)
                                            ) {
                                                Icon(
                                                    if (getPlayerState == PlayerStates.PAUSE ||
                                                        getPlayerState == PlayerStates.NULL
                                                    )
                                                        Icons.Default.PlayArrow
                                                    else Icons.Default.Pause, null
                                                )
                                            }
                                            Text(
                                                substringAfter(IS_RECORD),
                                                fontSize = 12.sp,
                                                modifier =
                                                Modifier.padding(end = 10.dp)
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = text,
                                            modifier = Modifier
                                                .padding(
                                                    top = 5.dp,
                                                    bottom = 5.dp,
                                                    start = 10.dp,
                                                    end = 10.dp
                                                ), fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Box(
                        contentAlignment = if (side == "green") Alignment.CenterEnd else Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = listMessages.getTimePadding(i, this@with, 15.dp),
                                top = listMessages.getTimePadding(i, this@with, 5.dp)
                            )
                    ) {
                        Text(
                            text = listMessages.getTimeText(i, this@with),
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
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
