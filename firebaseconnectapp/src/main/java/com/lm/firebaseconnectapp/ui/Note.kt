package com.lm.firebaseconnectapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lm.firebaseconnect.State.GET_INCOMING_CALL
import com.lm.firebaseconnect.callState
import com.lm.firebaseconnectapp.firebaseConnect

@Composable
fun Note(
    modifier: Modifier,
    notesText: String,
    i: String,
    online: String,
    navController: NavHostController,
    token: String,
    onCallClick: (String) -> Unit
) {
    Card(
        modifier = modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 5.dp, top = 5.dp)
            .fillMaxWidth()
            .height(80.dp)
            .clickable {
                firebaseConnect.setChatId(i.toInt())
                navController.navigate("chat")
            },
        shape = RoundedCornerShape(8.dp), border = BorderStroke(2.dp,
            if (callState.value.typeMessage == GET_INCOMING_CALL) Color.Red else {
                if (online == "1") Green else Color.Blue
            })
    ) {

        Box(Modifier.padding(10.dp), CenterStart) {
            Box(Modifier.padding(10.dp).fillMaxWidth(), CenterEnd) {
                Icon(
                    Icons.Default.Call, null, modifier = Modifier.clickable {
                        firebaseConnect.call(token)
                        onCallClick(token)
                    }
                )
            }
            Column {
                Text(
                    text = i, maxLines = 1,
                    fontSize = 12.sp, color = Color.Gray
                )
                if (notesText.isNotEmpty()) {
                    Text(
                        text = notesText, maxLines = 1,
                        fontSize = 12.sp, color = Color.Gray
                    )
                }
                Text(
                    text = online, maxLines = 1,
                    fontSize = 12.sp, color = Color.Gray
                )
            }
        }
    }
}
