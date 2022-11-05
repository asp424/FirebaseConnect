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
import com.lm.firebaseconnectapp.animDp
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onlineVisible: Boolean, onIconClick: () -> Unit) {
    with(mainDep) {
        with(uiStates) {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = getMainColor
                ), modifier = Modifier
                    .fillMaxWidth()
                    .offset(0.dp, animDp(getToolbarVisible, 0.dp, (-100).dp, 350))
                    .clickable(onClick = remember { { false.setSettingsVisible } }),
                navigationIcon = { SettingsIcon() }, title = {},
                actions = {
                    InfoBox(scale = 0.8f, userModel = getUserModelChat, onlineVisible) {}
                }
            )
        }
    }
}
