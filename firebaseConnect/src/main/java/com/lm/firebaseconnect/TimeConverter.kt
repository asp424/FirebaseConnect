package com.lm.firebaseconnect

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class TimeConverter {

    val currentTime get() = "${T_T_S}${calendar.time.time}${T_T_E}"

    fun String.currentTimeZoneTime() =
        "${substringBefore(T_T_S)}${parseTimestamp().getTime()}${substringAfter(T_T_E)}"


    fun String.parseTimestamp() = substringAfter(T_T_S).substringBefore(T_T_E)

    private fun String.getTime() = formatDate(this)

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(date: String): String {
        date.toLongOrNull()?.also {
            return SimpleDateFormat("H:mm").apply { timeZone = TimeZone.getDefault() }.format(it)
        }
        return "0"
    }

    private val calendar get() = Calendar.getInstance()

    companion object {
        const val T_T_S = "<T>"
        const val T_T_E = "</T>"
    }
}