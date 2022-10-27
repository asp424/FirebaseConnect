package com.lm.firebaseconnect

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.core.app.ComponentActivity

val <T> T.log get() = Log.d("My", toString())

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}
