package com.lm.firebaseconnect

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class TimeConverter {

    val currentTime get() = calendar.time.time

    fun String.parseTimestamp() = substringAfter(T_T_S).substringBefore(T_T_E)

    fun String.getTimeToMessage() = parseTimestamp().getTime()
    private fun String.getTime() = formatTime(this)

    @SuppressLint("SimpleDateFormat")
    private fun formatTime(date: String): String {
        date.toLongOrNull()?.also {
            return SimpleDateFormat("H:mm")
                .apply { timeZone = TimeZone.getDefault() }.format(it)
        }
        return "0"
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(date: String): String {
        date.toLongOrNull()?.also {
            return SimpleDateFormat("d MMMM")
                .apply { timeZone = TimeZone.getDefault() }.format(it)
        }
        return "0"
    }

    private val calendar get() = Calendar.getInstance()

    companion object {
        const val T_T_S = "<T>"
        const val T_T_E = "</T>"
    }
}