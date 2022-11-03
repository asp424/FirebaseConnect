package com.lm.firebaseconnectapp.ui.cells

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep

@Composable
fun SettingsIcon() {
    with(mainDep.uiStates) {
        Box(
            modifier = Modifier.scale(animScale(getIsMainMode)).padding(start = 20.dp)
        ) {
            Icon(
                Icons.Rounded.Settings, null, modifier = Modifier
                    .clickable(onClick = settingsIconClick), tint = getSecondColor
            )
        }
    }
}