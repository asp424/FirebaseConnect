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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.AnimDp
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.ui.UiStates.getIsMainMode
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import com.lm.firebaseconnectapp.ui.UiStates.getToolbarVisible
import com.lm.firebaseconnectapp.ui.UiStates.setNavState
import com.lm.firebaseconnectapp.ui.UiStates.setSettingsVisible
import com.lm.firebaseconnectapp.ui.cells.chat.cells.InfoBox
import com.lm.firebaseconnectapp.ui.cells.settings.SettingsIcon
import com.lm.firebaseconnectapp.ui.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    var y by remember { mutableStateOf(0.dp) }

    AnimDp(getToolbarVisible, 0.dp, (-100).dp, 350) { y = it }
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = if (isSystemInDarkTheme()) Color.Black else getMainColor
        ), modifier = Modifier
            .fillMaxWidth()
            .offset(0.dp, y)
            .clickable(onClick = remember { { false.setSettingsVisible } }),
        navigationIcon = {
            Icon(
                Icons.Default.ArrowBack, null, modifier =
                Modifier
                    .clickable { setNavState(NavRoutes.MAIN) }
                    .scale(animScale(!getIsMainMode))
                    .padding(start = 15.dp, top = 10.dp),
                tint = getSecondColor
            )
        }, title = {},
        actions = {
            SettingsIcon()
            InfoBox(0.8f)
        }
    )
}


