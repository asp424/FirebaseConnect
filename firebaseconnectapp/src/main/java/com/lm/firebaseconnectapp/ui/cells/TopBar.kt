package com.lm.firebaseconnectapp.ui.cells

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.States.listUsers
import com.lm.firebaseconnect.models.UIUsersStates
import com.lm.firebaseconnect.models.UserModel
import com.lm.firebaseconnectapp.animDp
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getToolbarVisible
import com.lm.firebaseconnectapp.ui.UiStates.setOnlineVisible
import com.lm.firebaseconnectapp.ui.UiStates.setSettingsVisible

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = getMainColor
        ), modifier = Modifier
            .fillMaxWidth()
            .offset(0.dp, animDp(getToolbarVisible, 0.dp, (-100).dp, 350))
            .clickable(onClick = remember { { false.setSettingsVisible } }),
        navigationIcon = { SettingsIcon() }, title = {},
        actions = {
            InfoBox(0.8f)
        }
    )
}

fun SPreferences.getChatModel(firebaseConnect: FirebaseConnect)
= if (listUsers.value is UIUsersStates.Success)
            (listUsers.value as UIUsersStates.Success).list.find {
                it.id == readChatId()
            }?.apply { firebaseConnect.setChatId(id.toInt()); setOnlineVisible(true) }
                ?: UserModel(name = "Empty") else UserModel(name = "Empty")

