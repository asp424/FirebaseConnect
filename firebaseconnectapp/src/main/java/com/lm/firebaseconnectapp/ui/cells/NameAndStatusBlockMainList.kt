package com.lm.firebaseconnectapp.ui.cells


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.firebaseconnect.UserModel
import com.lm.firebaseconnectapp.firebaseConnect
import com.lm.firebaseconnectapp.ui.theme.H7
import com.lm.firebaseconnectapp.ui.theme.H8

@Composable
fun NameAndStatusBlockMainList(
    userModel: UserModel, onCallClick: (UserModel) -> Unit
) {
    Column(
        Modifier.padding(start = 10.dp, 15.dp),
        Arrangement.Center, Alignment.CenterHorizontally,
    ) {

        Text(
            text = userModel.id,
            fontSize = 16.sp,
            style = H7,
            maxLines = 1, modifier = Modifier.width(
                LocalConfiguration.current.screenWidthDp.dp / 2.3f
            )
        )
    }
    Icon(
        Icons.Default.Call, null, modifier = Modifier.clickable {
            firebaseConnect.call(userModel.token)
            onCallClick(userModel)
        }.padding(start = 10.dp, 15.dp)
    )
}