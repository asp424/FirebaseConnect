package com.lm.firebaseconnect

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class TimeConverter {

    val currentTime get() = "${TIME_TAG_START}${calendar.time.time}${TIME_TAG_END}"

    fun String.currentTimeZoneTime() = "${substringBefore(TIME_TAG_START)}${
        getTime(substringAfter(TIME_TAG_START).substringBefore(TIME_TAG_END).toLong())
    }${substringAfter(TIME_TAG_END)}"

    private fun getTime(wasDate: Long) = with(formatDate("H:mm", wasDate)) {
        when (calendar.get(Calendar.DAY_OF_YEAR) - formatDate("D", wasDate).toInt()) {
            1 -> "yesterday at $this"
            0 -> this
            else -> formatDate("d:MM:yy ", wasDate)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(value: String, date: Long): String {
        return SimpleDateFormat(value).apply { timeZone = TimeZone.getDefault() }.format(date)
    }

    private val calendar get() = Calendar.getInstance()

    companion object{
        private const val TIME_TAG_START = "<T>"
        private const val TIME_TAG_END = "</T>"
    }
}