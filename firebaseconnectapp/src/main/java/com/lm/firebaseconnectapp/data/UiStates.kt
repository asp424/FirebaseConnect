package com.lm.firebaseconnectapp.data

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.lm.firebaseconnect.models.UserModel
import com.lm.firebaseconnectapp.ui.theme.main
import com.lm.firebaseconnectapp.ui.theme.second


@Immutable
@Stable
data class UiStates(
    private var userModelChat: MutableState<UserModel> = mutableStateOf(UserModel()),
    private var setSelectionEnable: MutableState<Boolean> = mutableStateOf(false),
    private var navState: MutableState<String> = mutableStateOf(""),
    private var clipboardIsEmpty: MutableState<Boolean> = mutableStateOf(false),
    private var isDeleteMode: MutableState<Boolean> = mutableStateOf(false),
    private var isFullscreenMode: MutableState<Boolean> = mutableStateOf(false),
    private var isMainMode: MutableState<Boolean> = mutableStateOf(true),
    private var isExpandShare: MutableState<Boolean> = mutableStateOf(false),
    private var settingsVisible: MutableState<Boolean> = mutableStateOf(false),
    private var notShareVisible: MutableState<Boolean> = mutableStateOf(true),
    private var chatId: MutableState<String> = mutableStateOf(""),
    val mainColor: MutableState<Color> = mutableStateOf(main),
    val secondColor: MutableState<Color> = mutableStateOf(second),
    val listDeleteAble: SnapshotStateList<String> = mutableStateListOf(),
    var selection: Pair<Int, Int> = Pair(-1, -1)
) {
    val getMainColor get() = mainColor.value
    val getNavState get() = navState.value
    val getUserModelChat get() = userModelChat.value
    val getSetSelectionEnable get() = setSelectionEnable.value
    val getChatId get() = chatId.value
    private val getNotShareVisible get() = notShareVisible.value
    val getSettingsVisible get() = settingsVisible.value
    val getSelection get() = selection
    val getIsExpandShare get() = isExpandShare.value
    val getIsMainMode get() = isMainMode.value
    val getIsFullscreenMode get() = isFullscreenMode.value
    val getIsDeleteMode get() = isDeleteMode.value
    private val getClipboardIsEmpty get() = clipboardIsEmpty.value
    val UserModel.setUserModelChat get() = run { userModelChat.value = this }
    private val Boolean.setIsDeleteMode get() = run { isDeleteMode.value = this }
    private val Boolean.setIsFullscreenMode get() = run { isFullscreenMode.value = this }
    val Boolean.setIsMainMode get() = run { isMainMode.value = this }
    private val Boolean.setIsExpandShare get() = run { isExpandShare.value = this }
    val Color.setMainColor get() = run { mainColor.value = this }
    val Color.setSecondColor get() = run { secondColor.value = this }

    private val Boolean.setSetSelectionEnable get() = run { setSelectionEnable.value = this }

    val String.setChatId get() = run { chatId.value = this }

    val String.setNavState get() = run { navState.value = this }

    private val Boolean.setNotShareVisible get() = run { notShareVisible.value = this }

    val Pair<Int, Int>.setSelection get() = run { selection = this }

    val Boolean.setClipboardIsEmpty get() = run { clipboardIsEmpty.value = this }

    val Boolean.setSettingsVisible get() = run { settingsVisible.value = this }

    val getSecondColor get() = secondColor.value

    val settingsIconClick
        @Composable get() = remember {
            {
                if (getSettingsVisible) false.setSettingsVisible else true.setSettingsVisible
            }
        }
}
