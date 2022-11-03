package com.lm.firebaseconnectapp.ui.cells

import android.os.Build
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.lm.firebaseconnectapp.appComponent
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.di.compose.MainDep
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.ui.RegScreen

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun SettingsCard() {
    with(mainDep) {
        with(uiStates) {
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
                    ), border = BorderStroke(3.dp, getMainColor)
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
                        val activity = LocalContext.current as MainActivity
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Button(
                                onClick = {
                                    firebaseAuth.signOut()
                                    firebaseConnect.setMyName("")
                                    firebaseConnect.setMyDigit("")
                                    firebaseConnect.setChatId(0)
                                    false.setSettingsVisible
                                    activity.setContent {
                                        MainDep(activity.appComponent) { RegScreen() }
                                    }
                                }, colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = getMainColor
                                )
                            ) { Text(text = "Sign out", color = getSecondColor) }
                        }
                    }
                }
            }
        }
    }
}
