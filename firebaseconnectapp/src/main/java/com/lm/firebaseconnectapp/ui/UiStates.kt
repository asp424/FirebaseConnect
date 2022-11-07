package com.lm.firebaseconnectapp.ui

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.lm.firebaseconnectapp.ui.navigation.NavRoutes
import com.lm.firebaseconnectapp.ui.theme.main
import com.lm.firebaseconnectapp.ui.theme.second

@Immutable
@Stable
object UiStates {
    private var toolbarVisible: MutableState<Boolean> = mutableStateOf(false)
    private var navState: MutableState<NavRoutes> = mutableStateOf(NavRoutes.EMPTY)
    private var isMainMode: MutableState<Boolean> = mutableStateOf(true)
    private var settingsVisible: MutableState<Boolean> = mutableStateOf(false)
    private val mainColor: MutableState<Color> = mutableStateOf(main)
    private val secondColor: MutableState<Color> = mutableStateOf(second)
    private val onlineVisible = mutableStateOf(false)
    val getMainColor get() = mainColor.value
    val getNavState get() = navState.value
    val getOnlineVisible get() = onlineVisible.value
    val getToolbarVisible get() = toolbarVisible.value
    val getSettingsVisible get() = settingsVisible.value
    val getIsMainMode get() = isMainMode.value
    fun setIsMainMode(boolean: Boolean) = run { isMainMode.value = boolean }
    val Color.setMainColor get() = run { mainColor.value = this }
    val Color.setSecondColor get() = run { secondColor.value = this }
    fun setToolbarVisible(boolean: Boolean) = run { toolbarVisible.value = boolean }
    fun setOnlineVisible(boolean: Boolean) = run { onlineVisible.value = boolean }
    fun setNavState(navRoute: NavRoutes) = run { navState.value = navRoute }
    val Boolean.setSettingsVisible get() = run { settingsVisible.value = this }
    val getSecondColor get() = secondColor.value
    val settingsIconClick
        @Composable get() = remember {
            { if (getSettingsVisible) false.setSettingsVisible else true.setSettingsVisible }
        }
}
