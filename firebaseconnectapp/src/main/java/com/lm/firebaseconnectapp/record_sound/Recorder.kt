package com.lm.firebaseconnectapp.record_sound

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.media.MediaPlayer
import android.media.MediaRecorder
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnectapp.showToast
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getPlayerState
import com.lm.firebaseconnectapp.ui.UiStates.getRecordState
import com.lm.firebaseconnectapp.ui.UiStates.getVoiceDuration
import com.lm.firebaseconnectapp.ui.UiStates.setButtonPlayOffset
import com.lm.firebaseconnectapp.ui.UiStates.setCurrentPlayTimestamp
import com.lm.firebaseconnectapp.ui.UiStates.setPlayerState
import com.lm.firebaseconnectapp.ui.UiStates.setRecordState
import com.lm.firebaseconnectapp.ui.UiStates.setVoiceDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class Recorder @Inject constructor(
    private val mediaRecorder:
    Function1<@JvmSuppressWildcards String, @JvmSuppressWildcards MediaRecorder?>,
    private val context: Application,
    private val firebaseConnect: FirebaseConnect
) {

    private var recorder: MediaRecorder? = null

    private var player: MediaPlayer? = null

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
                sendMessage("$IS_RECORD$it") {
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
        if (getPlayerState == PlayerStates.NULL) {
            player = MediaPlayer()
            player?.apply {
                runCatching {
                    if (url.startsWith("https")) {
                        setDataSource(url)
                        prepare()
                        setOnPreparedListener {
                            setVoiceDuration(duration.milliseconds)
                            onCreate()
                        }
                        setOnCompletionListener {
                            setPlayerState(PlayerStates.NULL)
                        }
                    } else context.showToast(url)
                }.onFailure {
                    context.showToast(it.message.toString())
                    setPlayerState(PlayerStates.NULL)
                }
            }
        }
    }

    fun play() {
        player?.apply {
            runCatching {
                start()
            }.onSuccess {
                setPlayerState(PlayerStates.PLAYING)
            }.onFailure { context.showToast(it.message.toString()) }
        }
    }

    fun String.stopAndPlay() {
        setVoiceDuration(ZERO)
        stopPlay {
            firebaseConnect.firebaseStorage.readSound(substringAfter(IS_RECORD)
                .apply { setCurrentPlayTimestamp(this) }) {
                if (it.isNotEmpty()) {
                    create(it) { play(); setButtonPlayOffset(false) }
                } else context.showToast("File not found")
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

    private fun stopPlay(onStop: () -> Unit) {
        runCatching {
            player?.apply {
                release()
                player = null
            }
        }.onSuccess {
            onStop()
            setPlayerState(PlayerStates.NULL)
        }.onFailure { context.showToast(it.message.toString()) }
    }

    private val String.filePath
        get() = File(
            ContextWrapper(context)
                .getDir("sounds", Context.MODE_PRIVATE), "${this}.mp3"
        )

    private val timeStamp get() = Calendar.getInstance().time.time

    companion object {
        const val IS_RECORD = "<*R>isRecord<*/R>"
    }
}
