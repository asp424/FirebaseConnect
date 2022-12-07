package com.lm.firebaseconnectapp.data

import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import com.chibde.visualizer.CircleBarVisualizer
import com.lm.firebaseconnectapp.ui.UiStates.setMainColor
import com.lm.firebaseconnectapp.ui.UiStates.setSecondColor
import javax.inject.Inject

interface SPreferences {

    fun saveIconUri(uri: Uri): SPreferences

    fun readIconUri(): Uri?

    fun saveMainColor(color: Int)

    fun readMainColor(): Int

    fun saveSecondColor(color: Int)

    fun readSecondColor(): Int

    fun saveName(id: String): SPreferences

    fun readName(): String

    fun saveMyId(id: String)

    fun readMyId(): String

    fun saveChatId(id: String): String

    fun readChatId(): String

    fun saveFirstIndex(i: Int): Int

    fun readFirstIndex(init: Int): Int

    fun saveFirstItemOffset(i: Int): Int

    fun readFirstItemOffset(init: Int): Int

    class Base @Inject constructor(
        private val sharedPreferences: SharedPreferences,
    ) : SPreferences {

        init {
                Color(readMainColor()).setMainColor
                Color(readSecondColor()).setSecondColor
        }
        override fun saveIconUri(uri: Uri) = apply {
            sharedPreferences.edit()
                .putString(Uri.EMPTY.toString(), uri.toString()).apply()
        }

        override fun saveChatId(id: String) = id.apply {
            sharedPreferences.edit().putString("chatId", id).apply()
        }

        override fun readChatId()
        = sharedPreferences.getString("chatId", "") ?: ""

        override fun saveFirstIndex(i: Int) = i.apply {
            sharedPreferences.edit().putInt("firstIndex", this).apply()
        }

        override fun readFirstIndex(init: Int) = sharedPreferences
            .getInt("firstIndex", init)

        override fun saveFirstItemOffset(i: Int) = i.apply {
            sharedPreferences.edit().putInt("firstIndexOffset", this).apply()
        }

        override fun readFirstItemOffset(init: Int) = sharedPreferences
            .getInt("firstIndexOffset", init)

        override fun readIconUri() = sharedPreferences
            .getString(Uri.EMPTY.toString(), "")?.toUri()

        override fun saveMainColor(color: Int) {
            sharedPreferences.edit().putInt("mainColor", color).apply()
        }

        override fun readMainColor() = sharedPreferences.getInt("mainColor", (0xFF00BCD4).toInt())

        override fun saveSecondColor(color: Int) {
            sharedPreferences.edit().putInt("secondColor", color).apply()
        }

        override fun readSecondColor() =
            sharedPreferences.getInt("secondColor", (0xFFFFFFFF).toInt())

        override fun saveName(id: String) = apply {
            sharedPreferences.edit().putString("name", id).apply()
        }

        override fun readName() = sharedPreferences.getString("name", "") ?: ""

        override fun saveMyId(id: String) {
            sharedPreferences.edit().putString("mYid", id).apply()
        }

        override fun readMyId() =
            sharedPreferences.getString("mYid", "") ?: ""
    }
}