package com.lm.firebaseconnectapp.data

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.lm.firebaseconnect.models.UserModel
import com.lm.firebaseconnectapp.ui.NavRoutes
import com.lm.firebaseconnectapp.ui.theme.main
import com.lm.firebaseconnectapp.ui.theme.second

@Immutable
@Stable
data class UiStates(
    private var userModelChat: MutableState<UserModel> = mutableStateOf(UserModel(id = "0")),
    private var toolbarVisible: MutableState<Boolean> = mutableStateOf(false),
    private var navState: MutableState<NavRoutes> = mutableStateOf(NavRoutes.EMPTY),
    private var isMainMode: MutableState<Boolean> = mutableStateOf(true),
    private var settingsVisible: MutableState<Boolean> = mutableStateOf(false),
    val mainColor: MutableState<Color> = mutableStateOf(main),
    val secondColor: MutableState<Color> = mutableStateOf(second),
    val listDeleteAble: SnapshotStateList<String> = mutableStateListOf(),
    var selection: Pair<Int, Int> = Pair(-1, -1)
) {
    val getMainColor get() = mainColor.value
    val getNavState get() = navState.value
    val getUserModelChat get() = userModelChat.value
    val getToolbarVisible get() = toolbarVisible.value
    val getSettingsVisible get() = settingsVisible.value
    val getIsMainMode get() = isMainMode.value
    val UserModel.setUserModelChat get() = run { userModelChat.value = this }
    val Boolean.setIsMainMode get() = run { isMainMode.value = this }
    val Color.setMainColor get() = run { mainColor.value = this }
    val Color.setSecondColor get() = run { secondColor.value = this }

    val Boolean.setToolbarVisible get() = run { toolbarVisible.value = this }

    val NavRoutes.setNavState get() = run { navState.value = this }

    val Boolean.setSettingsVisible get() = run { settingsVisible.value = this }

    val getSecondColor get() = secondColor.value

    val settingsIconClick
        @Composable get() = remember {
            {
                if (getSettingsVisible) false.setSettingsVisible else true.setSettingsVisible
            }
        }
}
