package com.lm.firebaseconnectapp.record_sound

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.media.MediaPlayer
import android.media.MediaRecorder
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.States.getListMessages
import com.lm.firebaseconnect.models.MessageModel
import com.lm.firebaseconnectapp.showToast
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getCurrentPlayTimestamp
import com.lm.firebaseconnectapp.ui.UiStates.getRecordState
import com.lm.firebaseconnectapp.ui.UiStates.getReplyMessage
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

    private var playList: List<String> = emptyList()

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
                sendMessage(
                    "$IS_RECORD$it",
                    if (getReplyVisible) getReplyMessage.key else ""
                ) {
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
            tryCatch({ recorder?.apply { stop(); release(); recorder = null } }, {
                setRecordState(RecordState.SAVING); onRelease()
            }, { setRecordState(RecordState.NULL) })
        }
    }

    private fun create(url: String, onCreate: () -> Unit) {
        playerSessionId.value = 0
        player = MediaPlayer()
        tryCatch({
            if (url.startsWith("https")) {
                player?.apply {
                    setDataSource(url)
                    prepareAsync()
                    setOnPreparedListener { onPrepare(onCreate) }
                    setOnCompletionListener { onComplete() }
                }
            } else context.showToast(url)
        }, onFailure = { setPlayerState(PlayerStates.NULL) })
    }

    fun play() {
        tryCatch({
            player?.start()
            playerSessionId.value = player?.audioSessionId ?: 0
            setPlayList()
        }, {
            setPlayerState(PlayerStates.PLAYING)
            setVoiceBarVisible(true)
            player?.playingTimer()
        }, { setPlayerState(PlayerStates.NULL) })
    }

    fun String.stopAndPlay() {
        setButtonPlayOffset(true)
        setVoiceDuration(ZERO)
        setVoiceLength(ZERO)
        stopPlay {
            firebaseConnect.firebaseStorage.readSound(apply { setCurrentPlayTimestamp(this) }) {
                if (it.isNotEmpty()) create(it) { play() }
            }
        }
    }

    fun pause() {
        player?.apply {
            if (isPlaying) {
                tryCatch({ pause() },
                    { setPlayerState(PlayerStates.PAUSE) }, { setPlayerState(PlayerStates.NULL) })
            }
        }
    }

    fun stopPlay(onStop: () -> Unit = {}) {
        setPlayerState(PlayerStates.NULL)
        tryCatch({ player?.release(); player = null }, { onStop() })
    }

    private val String.filePath
        get() = File(
            ContextWrapper(context).getDir("sounds", Context.MODE_PRIVATE), "${this}.mp3"
        )

    private fun onComplete() {
        playerSessionId.value = 0
        getPlayingMessage {
            if (getCurrentIndex != playList.lastIndex)
                playList[getCurrentIndex + 1].stopAndPlay()
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

    private fun setPlayList() { playList = getPlayingMessage() }

    private fun getPlayingMessage(onFind: String.() -> Unit = {}) =
        getListMessages { setPlayingInfo { onFind(this) } }

    private fun List<MessageModel>.setPlayingInfo(onSet: String.() -> Unit = {}) {
        findCurrentMessage()?.apply {
            playingSendTime.value = time
            playingSenderName.value = name
            onSet(voiceTimeStamp)
        }
    }

    private fun List<MessageModel>.findCurrentMessage() =
        find { it.voiceTimeStamp == getCurrentPlayTimestamp }

    private val String.getCurrentIndex get() = playList.indexOf(this)

    private val timeStamp get() = Calendar.getInstance().time.time

    private fun tryCatch(
        tryBlock: () -> Unit,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) = runCatching { tryBlock() }.onSuccess { onSuccess() }.onFailure {
        context.showToast(it.message.toString()); onFailure()
    }

    companion object {
        const val IS_RECORD = "<*R>isRecord<*/R>"
    }
}
