package com.lm.firebaseconnectapp.data

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.net.toUri
import javax.inject.Inject

interface SPreferences {

    fun saveIconUri(uri: Uri): SPreferences

    fun readIconUri(): Uri?

    fun saveMainColor(color: Int)

    fun readMainColor(): Int

    fun saveSecondColor(color: Int)

    fun readSecondColor(): Int

    fun setName(id: String): SPreferences

    fun getName(): String

    fun setMyId(id: String)

    fun getMyId(): String

    class Base @Inject constructor(
        private val sharedPreferences: SharedPreferences,
    ) : SPreferences {

        override fun saveIconUri(uri: Uri) = apply {
            sharedPreferences.edit()
                .putString(Uri.EMPTY.toString(), uri.toString()).apply()
        }

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

        override fun setName(id: String) = apply {
            sharedPreferences.edit().putString("name", id).apply()
        }

        override fun getName() = sharedPreferences.getString("name", "")?:""

        override fun setMyId(id: String) {
            sharedPreferences.edit().putString("mYid", id).apply()
        }

        override fun getMyId() =
            sharedPreferences.getString("mYid", "")?:""
    }
}