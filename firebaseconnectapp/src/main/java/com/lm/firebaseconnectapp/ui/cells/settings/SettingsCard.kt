package com.lm.firebaseconnectapp.ui.cells

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker
import com.godaddy.android.colorpicker.toColorInt
import com.lm.firebaseconnectapp.R
import com.lm.firebaseconnectapp.animScale
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.getSecondColor
import com.lm.firebaseconnectapp.ui.UiStates.getSettingsVisible
import com.lm.firebaseconnectapp.ui.UiStates.setMainColor
import com.lm.firebaseconnectapp.ui.UiStates.setSecondColor
import com.lm.firebaseconnectapp.ui.UiStates.setSettingsVisible
import com.lm.firebaseconnectapp.ui.UiStates.setToolbarVisible
import com.lm.firebaseconnectapp.ui.navigation.NavRoutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun SettingsCard() {
    val coroutine = rememberCoroutineScope()
    with(mainDep) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .scale(animScale(getSettingsVisible))
                        .fillMaxSize()
                        .padding(top = 56.dp), colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ), border = BorderStroke(3.dp, if (isSystemInDarkTheme()) Color.White else getMainColor)
                ) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp)
                        ) {
                            Text(text = stringResource(R.string.main_color))
                            HarmonyColorPicker(
                                Modifier
                                    .size(100.dp), ColorHarmonyMode.SHADES,
                                onColorChanged = { c ->
                                    Color(c.toColorInt()).setMainColor
                                    sPreferences.saveMainColor(c.toColorInt())
                                }
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp)
                        ) {
                            Text(text = stringResource(R.string.second_color))
                            HarmonyColorPicker(
                                Modifier
                                    .size(100.dp), ColorHarmonyMode.SHADES,
                                onColorChanged = { c ->
                                    Color(c.toColorInt()).setSecondColor
                                    sPreferences.saveSecondColor(c.toColorInt())
                                }
                            )
                        }
                    }
                    Column(Modifier.fillMaxSize().padding(bottom = 60.dp), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                        ) {
                        val activity = LocalContext.current as MainActivity
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Button(
                                onClick = {
                                    uiInteractor.signOutFromGoogle(activity.signInClient) {
                                        firebaseAuth.signOut()
                                        firebaseConnect.firebaseHandler.stopMainListener()
                                        coroutine.launch {
                                            false.setSettingsVisible
                                            delay(300)
                                            navController.navigate(NavRoutes.REG.route)
                                            setToolbarVisible(false)
                                        }
                                    }
                                }, colors = ButtonDefaults.buttonColors(if (isSystemInDarkTheme()) Color.Black else getMainColor)
                            ) { Text(text = "Sign out", color = if (isSystemInDarkTheme()) Color.White else getSecondColor) }
                        }
                    }
                }
            }
    }
}
