package com.lm.firebaseconnectapp.record_sound

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.media.MediaPlayer
import android.media.MediaRecorder
import com.lm.firebaseconnectapp.showToast
import com.lm.firebaseconnectapp.ui.UiStates.getRecordState
import com.lm.firebaseconnectapp.ui.UiStates.setPlayerState
import com.lm.firebaseconnectapp.ui.UiStates.setRecordState
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import javax.inject.Inject


class Recorder @Inject constructor(
    private val mediaRecorder:
    Function1<@JvmSuppressWildcards String, @JvmSuppressWildcards MediaRecorder?>,
    private val context: Application
) {

    private var recorder: MediaRecorder? = null

    private var player: MediaPlayer? = null

    private var timestampCurrent: String? = null

    fun startRecord() = timeStamp.apply {
        if (getRecordState == RecordState.NULL) {
            setRecordState(RecordState.PREPARE)
            timestampCurrent = toString()
            recorder = mediaRecorder(toString().filePath.toString())?.apply {
                runCatching {
                    prepare()
                    start()
                    setRecordState(RecordState.RECORDING)
                }
            }
        }
    }.toString()

    fun stopRecord() = timestampCurrent?.apply {
        if (getRecordState == RecordState.RECORDING) {
            setRecordState(RecordState.SAVING)
            recorder?.apply {
                runCatching {
                    stop()
                    release()
                    recorder = null
                }
            }
        }
    }

    fun play() {
        player?.apply {
            runCatching {
                start()
                setPlayerState(PlayerStates.PLAYING)
            }.onFailure { context.showToast(it.message.toString()) }
        }
    }

    fun create(file: ByteArray) {
        stopPlay()
        player = MediaPlayer()
        player?.apply {
            runCatching {
                val tempFile = "temp".filePath
                val fos = FileOutputStream(tempFile)
                fos.write(file)
                fos.close()
                setDataSource(tempFile.toString())
                prepare()
                setOnCompletionListener {
                    setPlayerState(PlayerStates.NULL)
                    tempFile.delete()
                }
            }.onFailure {
                context.showToast(it.message.toString())
            }
        }
    }

    fun pause() {
        player?.apply {
            if (isPlaying) {
                pause()
                setPlayerState(PlayerStates.PAUSE)
            }
        }
    }

    private fun stopPlay() {
        runCatching {
            player?.apply {
                release()
                player = null
            }
        }
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
