package com.lm.firebaseconnectapp.data

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.net.toUri
import com.lm.firebaseconnect.models.UserModel
import javax.inject.Inject

interface SPreferences {

    fun saveIconUri(uri: Uri): SPreferences

    fun readIconUri(): Uri?

    fun saveMainColor(color: Int)

    fun readMainColor(): Int

    fun saveSecondColor(color: Int)

    fun readSecondColor(): Int

    fun saveName(id: String): SPreferences

    fun getName(): String

    fun saveMyId(id: String)

    fun getMyId(): String

    fun saveChatUserModel(model: UserModel)

    fun readChatUserModel(): UserModel

    class Base @Inject constructor(
        private val sharedPreferences: SharedPreferences,
    ) : SPreferences {

        override fun saveIconUri(uri: Uri) = apply {
            sharedPreferences.edit()
                .putString(Uri.EMPTY.toString(), uri.toString()).apply()
        }

        override fun saveChatUserModel(model: UserModel) = with(model) {
            sharedPreferences.edit()
                .putString("userModelId", id)
                .putString("userModelName", name)
                .putString("userModelToken", token)
                .putString("userModelIcon", iconUri)
                .apply()
        }

        override fun readChatUserModel() = with(sharedPreferences) {
            UserModel(
                id = getString("userModelId", "") ?: "",
                name = getString("userModelName", "") ?: "",
                token = getString("userModelToken", "") ?: "",
                iconUri = getString("userModelIcon", "") ?: ""
            )
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

        override fun saveName(id: String) = apply {
            sharedPreferences.edit().putString("name", id).apply()
        }

        override fun getName() = sharedPreferences.getString("name", "") ?: ""

        override fun saveMyId(id: String) {
            sharedPreferences.edit().putString("mYid", id).apply()
        }

        override fun getMyId() =
            sharedPreferences.getString("mYid", "") ?: ""
    }
}