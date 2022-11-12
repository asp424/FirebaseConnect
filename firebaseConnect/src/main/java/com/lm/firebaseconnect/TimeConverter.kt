package com.lm.firebaseconnect

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class TimeConverter {

    val currentTime get() = "${T_T_S}${calendar.time.time}${T_T_E}"

    fun String.currentTimeZoneTime() =
        "${substringBefore(T_T_S)}${parseTimestamp().getTime()}${substringAfter(T_T_E)}"


    private fun String.parseTimestamp() = substringAfter(T_T_S).substringBefore(T_T_E)

    private fun String.getTime() = with(formatDate("H:mm", this)) {
        when (calendar.get(Calendar.DAY_OF_YEAR) - formatDate("D", this@getTime).toInt()) {
            1 -> "yesterday at $this"
            0 -> this
            else -> formatDate("d.MM.yy ", this@getTime)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(value: String, date: String): String {
        date.toLongOrNull()?.also {
            return SimpleDateFormat(value).apply { timeZone = TimeZone.getDefault() }.format(it)
        }
        return "0"
    }

    private val calendar get() = Calendar.getInstance()

    companion object{
        const val T_T_S = "<T>"
        const val T_T_E = "</T>"
    }
}