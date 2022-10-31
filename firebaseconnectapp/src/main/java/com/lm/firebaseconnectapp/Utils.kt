package com.lm.firebaseconnectapp

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.lm.firebaseconnectapp.core.App
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL

@Composable
fun animScale(target: Boolean, duration: Int = 300) = animateFloatAsState(
    if (target) 1f else 0f, tween(duration)
).value

val Context.appComponent get() =
    when(this){
        is App -> appComponent
        else -> (applicationContext as App).appComponent
    }

fun Context.longToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

fun startJitsiMit(context: Context, room: String) {
    JitsiMeetConferenceOptions.Builder().apply {
        setServerURL(URL("https://meet.jit.si"))
        setRoom("dfgafgerg43t5435")
        setUserInfo(JitsiMeetUserInfo().apply {
            displayName = "Пётр Геннадьевич"
        })
        setFeatureFlag("calendar.enabled", true)
        setFeatureFlag("toolbox.alwaysVisible", true)
        setFeatureFlag("welcomepage.enabled", false)
        setAudioOnly(true)
        JitsiMeetActivity.launch(context, build())
    }
}
