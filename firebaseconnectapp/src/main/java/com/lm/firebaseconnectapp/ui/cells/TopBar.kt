package com.lm.firebaseconnectapp.ui.cells

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.States.listUsers
import com.lm.firebaseconnect.models.UIUsersStates
import com.lm.firebaseconnect.models.UserModel
import com.lm.firebaseconnectapp.animDp
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.ui.UiStates.getIsMainMode
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import com.lm.firebaseconnectapp.ui.UiStates.getToolbarVisible
import com.lm.firebaseconnectapp.ui.UiStates.setNavState
import com.lm.firebaseconnectapp.ui.UiStates.setOnlineVisible
import com.lm.firebaseconnectapp.ui.UiStates.setSettingsVisible
import com.lm.firebaseconnectapp.ui.cells.chat.InfoBox
import com.lm.firebaseconnectapp.ui.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = if (isSystemInDarkTheme()) Color.Black else getMainColor
        ), modifier = Modifier
            .fillMaxWidth()
            .offset(0.dp, animDp(getToolbarVisible, 0.dp, (-100).dp, 350))
            .clickable(onClick = remember { { false.setSettingsVisible } }),
        navigationIcon = { SettingsIcon() }, title = {},
        actions = {
            Icon(
                Icons.Default.ArrowBack, null, modifier =
                Modifier
                    .clickable { setNavState(NavRoutes.MAIN) }
                    .scale(animScale(!getIsMainMode))
                    .padding(start = 20.dp),
                tint = getSecondColor
            )
            InfoBox(0.8f)
        }
    )
}

fun SPreferences.getChatModel(firebaseConnect: FirebaseConnect) =
    if (listUsers.value is UIUsersStates.Success)
        (listUsers.value as UIUsersStates.Success).list.find {
            it.id == readChatId()
        }?.apply {
            firebaseConnect.setChatId(id.toInt()); setOnlineVisible(true)
        } ?: UserModel(name = "Empty") else UserModel(name = "Empty")

