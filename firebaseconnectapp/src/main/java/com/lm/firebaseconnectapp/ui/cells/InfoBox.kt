package com.lm.firebaseconnectapp.ui.cells

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.models.UserModel
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep

@Composable
fun InfoBox(scale: Float, id: String, userModel: UserModel, onIconClick: () -> Unit) {
    with(userModel) {
        with(mainDep.firebaseConnect) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                Arrangement.Absolute.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
                    Row(
                        Modifier.wrapContentWidth(),
                        Arrangement.Start,
                        Alignment.CenterVertically
                    ) {
                        if ("a".isEmpty() && name.isNotEmpty())
                            DrawCircle(name, onClick = onIconClick)
                        else SetImage(
                            "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png",
                            onIconClick
                        )
                        Text(text = id, modifier = Modifier.padding(start = 6.dp))
                    }
                }

                Icon(
                    Icons.Default.Call, null, modifier = Modifier
                        .clickable(
                            onClick = remember {
                                {
                                    remoteMessages.initialCall(userModel)
                                }
                            })
                        .padding(end = 10.dp)
                )
            }
        }
    }
}