package com.lm.firebaseconnectapp

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.lm.firebaseconnect.States.remoteMessageModel
import com.lm.firebaseconnect.States.set
import com.lm.firebaseconnectapp.core.App
import com.lm.firebaseconnectapp.presentation.MainActivity

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


    fun startJitsiMit(context: Context, room: String) {
        /* JitsiMeetConferenceOptions.Builder().apply {
            setServerURL(URL("https://meet.jit.si"))
            setRoom(room)
            setUserInfo(JitsiMeetUserInfo().apply {
                displayName = "Пётр Геннадьевич"
            })
            setFeatureFlag("calendar.enabled", true)
            setFeatureFlag("toolbox.alwaysVisible", true)
            setFeatureFlag("welcomepage.enabled", false)
            setAudioOnly(true)
            JitsiMeetActivity.launch(context, build())
        */
        // callState.value = remoteMessageModel.busy
        remoteMessageModel.testBusy.set
    }


