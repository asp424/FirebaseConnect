package com.lm.firebaseconnectapp.ui

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.lm.firebaseconnect.models.UserModel
import com.lm.firebaseconnectapp.record_sound.PlayerStates
import com.lm.firebaseconnectapp.record_sound.RecordState
import com.lm.firebaseconnectapp.ui.navigation.NavRoutes
import com.lm.firebaseconnectapp.ui.theme.main
import com.lm.firebaseconnectapp.ui.theme.second
import kotlin.time.Duration

@Immutable
@Stable
object UiStates {
    private var toolbarVisible: MutableState<Boolean> = mutableStateOf(false)
    private var playerState: MutableState<PlayerStates> = mutableStateOf(PlayerStates.NULL)
    private var currentPlayTimestamp: MutableState<String> = mutableStateOf("")
    private var voiceDuration: MutableState<Duration> = mutableStateOf(Duration.ZERO)
    private var buttonPlayOffset: MutableState<Boolean> = mutableStateOf(false)
    private var recordState: MutableState<RecordState> = mutableStateOf(RecordState.NULL)
    private var navState: MutableState<NavRoutes> = mutableStateOf(NavRoutes.EMPTY)
    private var isMainMode: MutableState<Boolean> = mutableStateOf(true)
    private var settingsVisible: MutableState<Boolean> = mutableStateOf(false)
    private val mainColor: MutableState<Color> = mutableStateOf(main)
    private val secondColor: MutableState<Color> = mutableStateOf(second)
    private val onlineVisible = mutableStateOf(false)
    val getMainColor get() = mainColor.value

    val getVoiceDuration get() = voiceDuration.value

    val getButtonPlayOffset get() = buttonPlayOffset.value

    val getPlayerState get() = playerState.value

    val getCurrentPlayTimestamp get() = currentPlayTimestamp.value

    val getRecordState get() = recordState.value
    val getNavState get() = navState.value
    val getOnlineVisible get() = onlineVisible.value
    val getToolbarVisible get() = toolbarVisible.value
    val getSettingsVisible get() = settingsVisible.value
    val getIsMainMode get() = isMainMode.value
    fun setIsMainMode(boolean: Boolean) = run { isMainMode.value = boolean }

    fun setButtonPlayOffset(boolean: Boolean) = run { buttonPlayOffset.value = boolean }

    fun setCurrentPlayTimestamp(timestamp: String) = run { currentPlayTimestamp.value = timestamp }

    fun setVoiceDuration(duration: Duration) = run { voiceDuration.value = duration }
    val Color.setMainColor get() = run { mainColor.value = this }
    val Color.setSecondColor get() = run { secondColor.value = this }
    fun setToolbarVisible(boolean: Boolean) = run { toolbarVisible.value = boolean }
    fun setOnlineVisible(boolean: Boolean) = run { onlineVisible.value = boolean }
    fun setNavState(navRoute: NavRoutes) = run { navState.value = navRoute }

    fun setPlayerState(state: PlayerStates) = run { playerState.value = state }

    fun setRecordState(state: RecordState) = run { recordState.value = state }

    val Boolean.setSettingsVisible get() = run { settingsVisible.value = this }
    val getSecondColor get() = secondColor.value
    val settingsIconClick
        @Composable get() = remember {
            { if (getSettingsVisible) false.setSettingsVisible else true.setSettingsVisible }
        }
}
