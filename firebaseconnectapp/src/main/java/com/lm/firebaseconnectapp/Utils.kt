package com.lm.firebaseconnectapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lm.firebaseconnect.States.remoteMessageModel
import com.lm.firebaseconnect.States.set
import com.lm.firebaseconnect.log
import com.lm.firebaseconnectapp.core.App
import com.lm.firebaseconnectapp.presentation.MainActivity
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
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
        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
    }
}

fun MainActivity.showToast(text: String) = toast(this, text)

fun startJitsiMit(context: Context, room: String, name: String, icon: String) {
    JitsiMeetConferenceOptions.Builder().apply {
        setServerURL(URL("https://meet.jit.si"))
        setRoom(room)
        setUserInfo(JitsiMeetUserInfo().apply {
            displayName = name
            avatar = URL(icon)
        })
        setConfigOverride("prejoinPageEnabled", false)
        setFeatureFlag("prejoinPageEnabled", false)
        setFeatureFlag("invite.enabled",false)
        setAudioOnly(true)
        JitsiMeetActivity.launch(context, build())

        remoteMessageModel.busy.set
        // remoteMessageModel.testBusy.set
        registerForBroadcastMessages(context)
    }
}

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




