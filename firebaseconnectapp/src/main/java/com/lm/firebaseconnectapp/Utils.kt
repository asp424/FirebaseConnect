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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lm.firebaseconnect.FirebaseConnect
import com.lm.firebaseconnect.States
import com.lm.firebaseconnect.States.ANSWER
import com.lm.firebaseconnect.States.BUSY
import com.lm.firebaseconnect.States.GET_CHECK_FOR_CALL
import com.lm.firebaseconnect.States.GET_INCOMING_CALL
import com.lm.firebaseconnect.States.OUTGOING_CALL
import com.lm.firebaseconnect.States.REJECT
import com.lm.firebaseconnect.States.RESET
import com.lm.firebaseconnect.States.WAIT
import com.lm.firebaseconnect.States.get
import com.lm.firebaseconnect.States.remoteMessageModel
import com.lm.firebaseconnect.States.set
import com.lm.firebaseconnect.log
import com.lm.firebaseconnect.models.UIUsersStates
import com.lm.firebaseconnect.models.UserModel
import com.lm.firebaseconnectapp.core.App
import com.lm.firebaseconnectapp.data.SPreferences
import com.lm.firebaseconnectapp.databinding.VisualizerBinding
import com.lm.firebaseconnectapp.di.compose.MainDep.mainDep
import com.lm.firebaseconnectapp.presentation.MainActivity
import com.lm.firebaseconnectapp.ui.UiStates
import com.lm.firebaseconnectapp.ui.UiStates.getMainColor
import com.lm.firebaseconnectapp.ui.UiStates.playerSessionId
import kotlinx.coroutines.delay
import java.net.URL

@Composable
fun animScale(target: Boolean, duration: Int = 300) = animateFloatAsState(
    if (target) 1f else 0f, tween(duration)
).value

@Composable
fun animDp(target: Boolean, first: Dp, second: Dp, delay: Int = 300) = animateDpAsState(
    if (target) first else second, tween(delay)
).value

val Context.appComponent
    get() =
        when (this) {
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
    if (States.listUsers.value is UIUsersStates.Success)
        (States.listUsers.value as UIUsersStates.Success).list.find {
            it.id == readChatId()
        }?.apply {
            firebaseConnect.setChatId(id.toInt()); UiStates.setOnlineVisible(true)
        } ?: UserModel(name = "Empty") else UserModel(name = "Empty")

fun startJitsiMit(context: Context, room: String, name: String, icon: String) {
   /* JitsiMeetConferenceOptions.Builder().apply {
        setServerURL(URL("https://meet.jit.si"))
        setRoom(room)
        setUserInfo(JitsiMeetUserInfo().apply {
            displayName = name
            avatar = URL(icon)
        })
        setConfigOverride("prejoinPageEnabled", false)
        setFeatureFlag("prejoinPageEnabled", false)
        setFeatureFlag("invite.enabled", false)
        setAudioOnly(true)
        JitsiMeetActivity.launch(context, build())

        registerForBroadcastMessages(context)
    }

    */
}
/*
private fun registerForBroadcastMessages(context: Context) {
    val intentFilter = IntentFilter()

    for (type in BroadcastEvent.Type.values()) {
        intentFilter.addAction(type.action)
    }

    LocalBroadcastManager.getInstance(context)
        .registerReceiver(broadcastReceiver, intentFilter)
}

private val broadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.apply { onBroadcastReceived(intent, context) }
    }
}

private fun onBroadcastReceived(intent: Intent?, context: Context) {
    if (intent != null) {
        val event = BroadcastEvent(intent)
        when (event.type) {
            BroadcastEvent.Type.READY_TO_CLOSE -> {
                remoteMessageModel.rejectCall.set
                LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver)
            }

            else -> event.type.log
        }
    }
}

 */

@Composable
fun SoundController() {
    with(mainDep) {
        LaunchedEffect(get) {
            when (get.typeMessage) {

                OUTGOING_CALL -> waitSound.start()

                GET_CHECK_FOR_CALL -> {
                    waitSound.stop()
                    waitSound.prepareAsync()
                    callSound.start()
                }
                REJECT -> {
                    waitSound.stop()
                    waitSound.prepareAsync()
                    callSound.stop()
                    callSound.prepareAsync()
                }

                WAIT -> {
                    waitSound.stop()
                    waitSound.prepareAsync()
                    callSound.stop()
                    callSound.prepareAsync()
                }
                else -> {}
            }
        }
    }
}






