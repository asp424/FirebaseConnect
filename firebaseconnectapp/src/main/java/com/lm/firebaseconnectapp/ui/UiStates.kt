package com.lm.firebaseconnectapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.record_sound.PlayerStates
import com.lm.firebaseconnectapp.record_sound.RecordState
import com.lm.firebaseconnectapp.ui.navigation.NavRoutes
import com.lm.firebaseconnectapp.ui.theme.main
import com.lm.firebaseconnectapp.ui.theme.second
import kotlin.time.Duration

@Stable
object UiStates {
    private var toolbarVisible: MutableState<Boolean> = mutableStateOf(false)
    private var replyVisible: MutableState<Boolean> = mutableStateOf(false)
    private var replyMessage: MutableState<MessageModel> = mutableStateOf(MessageModel())
    private var dateCardVisible: MutableState<Boolean> = mutableStateOf(false)
    private var voiceBarVisible: MutableState<Boolean> = mutableStateOf(false)
    private var playerState: MutableState<PlayerStates> = mutableStateOf(PlayerStates.NULL)
    private var currentPlayTimestamp: MutableState<String> = mutableStateOf("")
    private var currentDateAtScroll: MutableState<String> = mutableStateOf("")
    var playerSessionId: MutableState<Int> = mutableStateOf(0)
    var playingSendTime: MutableState<String> = mutableStateOf("")
    var playingSenderName: MutableState<String> = mutableStateOf("")
    private var voiceDuration: MutableState<Duration> = mutableStateOf(Duration.ZERO)
    private var voiceLength: MutableState<Duration> = mutableStateOf(Duration.ZERO)
    private var buttonPlayOffset: MutableState<Boolean> = mutableStateOf(false)
    private var recordState: MutableState<RecordState> = mutableStateOf(RecordState.NULL)
    private var navState: MutableState<NavRoutes> = mutableStateOf(NavRoutes.EMPTY)
    private var isMainMode: MutableState<Boolean> = mutableStateOf(true)
    private var settingsVisible: MutableState<Boolean> = mutableStateOf(false)
    private val mainColor: MutableState<Color> = mutableStateOf(main)
    private val secondColor: MutableState<Color> = mutableStateOf(second)
    private val onlineVisible = mutableStateOf(false)
    private val unreadIndex = mutableStateOf(-1)
    val getMainColor get() = mainColor.value

    val getReplyMessage get() = replyMessage.value

    val getReplyVisible get() = replyVisible.value

    val getUnreadIndex get() = unreadIndex.value

    val getDateCardVisible get() = dateCardVisible.value

    val getCurrentDateAtScroll get() = currentDateAtScroll.value

    val getVoiceDuration get() = voiceDuration.value

    val getVoiceLength get() = voiceLength.value

    val getButtonPlayOffset get() = buttonPlayOffset.value

    val getPlayerState get() = playerState.value

    val getCurrentPlayTimestamp get() = currentPlayTimestamp.value

    val getRecordState get() = recordState.value

    val getNavState get() = navState.value

    val getOnlineVisible get() = onlineVisible.value

    val getToolbarVisible get() = toolbarVisible.value

    val getVoiceBarVisible get() = voiceBarVisible.value

    val getSettingsVisible get() = settingsVisible.value

    val getIsMainMode get() = isMainMode.value

    fun setIsMainMode(boolean: Boolean) = run { isMainMode.value = boolean }

    fun setReplyMessage(messageModel: MessageModel) = run { replyMessage.value = messageModel }

    fun setReplyVisible(boolean: Boolean) = run { replyVisible.value = boolean }

    fun setUnreadIndex(index: Int) = run { unreadIndex.value = index }

    fun setCurrentDateAtScroll(date: String) = run { currentDateAtScroll.value = date }

    fun setDateCardVisible(boolean: Boolean) = run { dateCardVisible.value = boolean }

    fun setButtonPlayOffset(boolean: Boolean) = run { buttonPlayOffset.value = boolean }

    fun setCurrentPlayTimestamp(timestamp: String) = run { currentPlayTimestamp.value = timestamp }

    fun setVoiceDuration(duration: Duration) = run { voiceDuration.value = duration }

    fun setVoiceLength(duration: Duration) = run { voiceLength.value = duration }

    val Color.setMainColor get() = run { mainColor.value = this }

    val Color.setSecondColor get() = run { secondColor.value = this }

    fun setToolbarVisible(boolean: Boolean) = run { toolbarVisible.value = boolean }

    fun setOnlineVisible(boolean: Boolean) = run { onlineVisible.value = boolean }

    fun setVoiceBarVisible(boolean: Boolean) = run { voiceBarVisible.value = boolean }

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




