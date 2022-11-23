package com.lm.firebaseconnectapp.record_sound

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.media.MediaPlayer
import android.media.MediaRecorder
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.States.listMessages
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnect.models.TypeMessage
import com.lm.firebaseconnect.models.UIMessagesStates
import com.lm.firebaseconnectapp.showToast
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getCurrentPlayTimestamp
import com.lm.firebaseconnectapp.ui.UiStates.getRecordState
import com.lm.firebaseconnectapp.ui.UiStates.getReplyVisible
import com.lm.firebaseconnectapp.ui.UiStates.playerSessionId
import com.lm.firebaseconnectapp.ui.UiStates.playingSendTime
import com.lm.firebaseconnectapp.ui.UiStates.playingSenderName
import com.lm.firebaseconnectapp.ui.UiStates.setButtonPlayOffset
import com.lm.firebaseconnectapp.ui.UiStates.setCurrentPlayTimestamp
import com.lm.firebaseconnectapp.ui.UiStates.setPlayerState
import com.lm.firebaseconnectapp.ui.UiStates.setRecordState
import com.lm.firebaseconnectapp.ui.UiStates.setVoiceBarVisible
import com.lm.firebaseconnectapp.ui.UiStates.setVoiceDuration
import com.lm.firebaseconnectapp.ui.UiStates.setVoiceLength
import com.lm.firebaseconnectapp.ui.cells.chat.message.voice.playingTimer
import java.io.File
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds


class Recorder @Inject constructor(
    private val mediaRecorder: Function1<@JvmSuppressWildcards String, @JvmSuppressWildcards MediaRecorder?>,
    private val context: Application,
    private val firebaseConnect: FirebaseConnect
) {

    private var currentList: List<String> = emptyList()

    private var recorder: MediaRecorder? = null

    var player: MediaPlayer? = null

    private var timestampCurrent: String? = null

    fun startRecord() = timeStamp.apply {
        if (getRecordState == RecordState.NULL) {
            timestampCurrent = toString()
            recorder = mediaRecorder(toString().filePath.toString())?.apply {
                runCatching {
                    prepare()
                    start()
                }.onSuccess {
                    setRecordState(RecordState.RECORDING)
                }.onFailure { context.showToast(it.message.toString()) }
            }
        }
    }.toString()

    fun stopRecord() = timestampCurrent?.also {
        release {
            with(firebaseConnect) {
                sendMessage("$IS_RECORD$it", if (getReplyVisible) UiStates.getReplyMessage.key else "") {
                    firebaseStorage.saveSound(context, it) { res ->
                        if (res.isNotEmpty()) context.showToast(res)
                    }
                }
                setRecordState(RecordState.NULL)
            }
        }
    }

    private fun release(onRelease: () -> Unit) {
        if (getRecordState == RecordState.RECORDING) {
            recorder?.apply {
                runCatching {
                    stop()
                    release()
                    recorder = null
                }.onSuccess {
                    setRecordState(RecordState.SAVING)
                    onRelease()
                }.onFailure {
                    context.showToast(it.message.toString())
                    setRecordState(RecordState.NULL)
                }
            }
        }
    }

    private fun create(url: String, onCreate: () -> Unit) {
        playerSessionId.value = 0
        player = MediaPlayer()
        player?.apply {
            runCatching {
                if (url.startsWith("https")) {
                    setDataSource(url)
                    prepareAsync()
                    setOnPreparedListener { onPrepare(onCreate) }
                    setOnCompletionListener { onComplete() }
                } else context.showToast(url)
            }.onFailure {
                context.showToast(it.message.toString())
                setPlayerState(PlayerStates.NULL)
            }
        }
    }

    fun play() {
        player?.apply {
            runCatching {
                start()
                playerSessionId.value = player?.audioSessionId ?: 0
                setCurrentList()
            }.onSuccess {
                setPlayerState(PlayerStates.PLAYING)
                setVoiceBarVisible(true)
                playingTimer(this)
            }.onFailure { context.showToast(it.message.toString()) }
        }
    }

    fun String.stopAndPlay() {
        setButtonPlayOffset(true)
        setVoiceDuration(ZERO)
        setVoiceLength(ZERO)
        stopPlay {
            firebaseConnect.firebaseStorage.readSound(
                apply { setCurrentPlayTimestamp(this) }) {
                if (it.isNotEmpty()) create(it) { play() }
                 else context.showToast("File not found")
            }
        }
    }

    fun pause() {
        player?.apply {
            if (isPlaying) {
                runCatching {
                    pause()
                }.onSuccess {
                    setPlayerState(PlayerStates.PAUSE)
                }.onFailure {
                    context.showToast(it.message.toString())
                    setPlayerState(PlayerStates.NULL)
                }
            }
        }
    }

    fun stopPlay(onStop: () -> Unit = {}) {
        setPlayerState(PlayerStates.NULL)
        runCatching {
            player?.apply {
                release()
                player = null
            }
        }.onSuccess {
            onStop()
        }.onFailure { context.showToast(it.message.toString()) }
    }

    private val String.filePath
        get() = File(
            ContextWrapper(context).getDir("sounds", Context.MODE_PRIVATE), "${this}.mp3"
        )

    private fun onComplete() {
        playerSessionId.value = 0
        getPlayingMessage {
            if (getCurrentIndex != currentList.lastIndex)
                currentList[getCurrentIndex + 1].stopAndPlay()
            else {
                setPlayerState(PlayerStates.NULL)
                playingSendTime.value = ""
                playingSenderName.value = ""
                setVoiceBarVisible(false)
            }
        }
        setVoiceDuration(ZERO)
    }

    private fun onPrepare(onDo: () -> Unit) {
        player?.apply {
            setVoiceDuration(duration.milliseconds)
            setVoiceLength(duration.milliseconds)
            playerSessionId.value = player?.audioSessionId ?: 0
            onDo()
        }
    }

    private fun setCurrentList() {
        currentList = if (listMessages.value is UIMessagesStates.Success) {
            with((listMessages.value as UIMessagesStates.Success).list) {
                findCurrentMessage()?.apply {
                    time.setCurrentSendingTime(); name.setCurrentSenderName()
                }
                filter { it.type == TypeMessage.VOICE }.map { it.voiceTimeStamp }
            }
        } else emptyList()
    }

    private fun getPlayingMessage(onFind: String.() -> Unit) {
        if (listMessages.value is UIMessagesStates.Success) {
            with((listMessages.value as UIMessagesStates.Success).list) {
                findCurrentMessage()?.apply {
                    time.setCurrentSendingTime(); name.setCurrentSenderName()
                    onFind(voiceTimeStamp)
                }
            }
        }
    }

    private fun List<MessageModel>.findCurrentMessage() =
        find { it.voiceTimeStamp == getCurrentPlayTimestamp }

    private fun String.setCurrentSendingTime() {
        playingSendTime.value = this
    }

    private fun String.setCurrentSenderName() {
        playingSenderName.value = this
    }

    private val String.getCurrentIndex get() = currentList.indexOf(this)

    private val timeStamp get() = Calendar.getInstance().time.time

    companion object {
        const val IS_RECORD = "<*R>isRecord<*/R>"
    }
}
