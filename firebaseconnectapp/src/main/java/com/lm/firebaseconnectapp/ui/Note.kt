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
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lm.firebaseconnect.State.GET_INCOMING_CALL
import com.lm.firebaseconnect.UserModel
import com.lm.firebaseconnect.callState
import com.lm.firebaseconnectapp.firebaseConnect

@Composable
fun Note(
    userModel: UserModel,
    navController: NavHostController,
    onCallClick: (UserModel) -> Unit
) {
    with(userModel) {
        Card(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, bottom = 5.dp, top = 5.dp)
                .fillMaxWidth()
                .height(80.dp)
                .clickable {
                    firebaseConnect.setChatId(id.toInt())
                    navController.navigate("chat")
                },
            shape = RoundedCornerShape(8.dp), border = BorderStroke(2.dp, Blue)
        ) {

            Box(Modifier.padding(10.dp), CenterStart) {
                Box(Modifier.padding(10.dp).fillMaxWidth(), CenterEnd) {
                    Icon(
                        Icons.Default.Call, null, modifier = Modifier.clickable {
                            firebaseConnect.call(token)
                            onCallClick(userModel)
                        }
                    )
                }
                Column {
                    Text(
                        text = id, maxLines = 1,
                        fontSize = 12.sp, color = Color.Gray
                    )
                    if (isWriting.isNotEmpty()) {
                        Text(
                            text = isWriting, maxLines = 1,
                            fontSize = 12.sp, color = Color.Gray
                        )
                    }
                    Text(
                        text = onLine, maxLines = 1,
                        fontSize = 12.sp, color = Color.Gray
                    )
                }
            }
        }
    }
}
