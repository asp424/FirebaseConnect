package com.lm.firebaseconnectapp

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.FirebaseRead
import com.lm.firebaseconnect.States
import com.lm.firebaseconnect.States.ANSWER
import com.lm.firebaseconnect.States.GET_CHECK_FOR_CALL
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.WAIT
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.remoteMessageModel
import com.lm.firebaseconnect.States.set
import com.lm.firebaseconnect.log
import com.lm.firebaseconnect.models.Nodes
import com.lm.firebaseconnect.models.UIUsersStates
import com.lm.firebaseconnect.models.UserModel
import com.lm.firebaseconnectapp.core.App
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.databinding.VisualizerBinding
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.playerSessionId
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL


@Composable
fun animScale(target: Boolean, duration: Int = 300) = animateFloatAsState(
    if (target) 1f else 0f, tween(duration)
).value

@Composable
fun AnimDp(target: Boolean, first: Dp, second: Dp, delay: Int = 300, onChange: (Dp) -> Unit) =
    with(animateDpAsState(if (target) first else second, tween(delay))) {
        LaunchedEffect(true) {
            withContext(IO) { snapshotFlow { value }.collect { onChange(it) } }
        }
    }

val Context.appComponent
    get() = when (this) {
        is App -> appComponent
        else -> (applicationContext as App).appComponent
    }

val toast: Context.(String) -> Unit by lazy {
    {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }
}

fun Context.showToast(text: String) = toast(this, text)

val getVisualizer: Activity.() -> View by lazy {
    {
        VisualizerBinding.inflate(layoutInflater).root.apply {
            setColor(getMainColor.toArgb())
            setPlayer(playerSessionId.value)
        }
    }
}

fun SPreferences.getChatModel(firebaseConnect: FirebaseConnect) =
    if (States.listUsers.value is UIUsersStates.Success) (States.listUsers.value as UIUsersStates.Success).list.find {
        it.id == readChatId()
    }?.apply {
        firebaseConnect.setChatId(id.toInt()); UiStates.setOnlineVisible(true)
    } ?: UserModel(name = "Empty") else UserModel(name = "Empty")

fun startJitsiMit(
    context: Context,
    room: String,
    name: String,
    icon: String
) {
    val defaultOptions =
        JitsiMeetConferenceOptions.Builder().setServerURL(URL("https://meet.jit.si"))
            .setUserInfo(JitsiMeetUserInfo().apply {
                displayName = name
                avatar = URL(icon)
            })
            //.setFeatureFlag("toolbox.enabled", false)
            .setFeatureFlag("filmstrip.enabled", false)
            .setFeatureFlag("welcomepage.enabled", false)
            .setFeatureFlag("prejoinpage.enabled", false)
            .setFeatureFlag("invite.enabled", false)
            .setFeatureFlag("raise-hand.enabled", false)
            .setFeatureFlag("calendar.enabled", false)
            .setFeatureFlag("car-mode.enabled", false)
            .setFeatureFlag("conference-timer.enabled", false)
            .setFeatureFlag("chat.enabled", false)
            .setFeatureFlag("help.enabled", false)
            .setFeatureFlag("invite.enabled", false)
            .setFeatureFlag("android.screensharing.enabled", false)
            .setFeatureFlag("kick-out.enabled", false)
            .setFeatureFlag("live-streaming.enabled", false)
            .setFeatureFlag("lobby-mode.enabled", false)
            .setFeatureFlag("meeting-name.enabled", false)
            .setFeatureFlag("meeting-password.enabled", false)
            .setFeatureFlag("overflow-menu.enabled", false)
            .setFeatureFlag("pip.enabled", false)
            .setFeatureFlag("raise-hand.enabled", false)
            .setFeatureFlag("reactions.enabled", false)
            .setFeatureFlag("recording.enabled", false)
            .setFeatureFlag("security-options.enabled", false)
            .setFeatureFlag("server-url-change.enabled", false)
            .setFeatureFlag("settings.enabled", false)
            .setFeatureFlag("tile-view.enabled", false)
            .setFeatureFlag("tile-view.enabled", true)
            .setFeatureFlag("video-share.enabled", false)
            .build()
    JitsiMeet.setDefaultConferenceOptions(defaultOptions)
    val options = JitsiMeetConferenceOptions.Builder().setRoom(room).setVideoMuted(true).build()
    JitsiMeetActivity.launch(context, options)
    registerForBroadcastMessages(context)
}

private fun registerForBroadcastMessages(context: Context) {
    val intentFilter = IntentFilter()

    for (type in BroadcastEvent.Type.values()) { intentFilter.addAction(type.action) }

    LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter)
}

private val broadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.apply { onBroadcastReceived(intent, context) }
    }
}

private fun onBroadcastReceived(intent: Intent?, context: Context) {
    intent?.apply {
        val event = BroadcastEvent(this)
        val receiver = LocalBroadcastManager.getInstance(context.applicationContext)
        when (event.type) {
            BroadcastEvent.Type.PARTICIPANT_LEFT -> {
                receiver.sendBroadcast(Intent("org.jitsi.meet.HANG_UP"))
                receiver.unregisterReceiver(broadcastReceiver)
                remoteMessageModel.rejectCall.set
            }
            BroadcastEvent.Type.READY_TO_CLOSE -> {
                receiver.unregisterReceiver(broadcastReceiver)
                remoteMessageModel.rejectCall.set
            }
            else -> Unit
        }
    }
}

@Composable
fun SoundController() {
    with(mainDep) {
        LaunchedEffect(get) {
            when (get.typeMessage) {

                OUTGOING_CALL -> {
                    waitSound.prepare()
                    callSound.prepare()
                    waitSound.start()
                }

                GET_CHECK_FOR_CALL -> {
                    waitSound.stop()
                    callSound.start()
                }

                REJECT -> {
                    waitSound.stop()
                    callSound.stop()
                }

                WAIT -> {
                    waitSound.stop()
                    callSound.stop()
                }

                ANSWER -> {
                    waitSound.stop()
                    callSound.stop()
                }

                else -> {}
            }
        }
    }
}






