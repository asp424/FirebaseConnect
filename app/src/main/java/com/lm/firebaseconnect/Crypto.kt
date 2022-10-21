package com.lm.firebaseconnect

import android.os.Build
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Crypto(private val cryptoKey: String) {

    fun cipherEncrypt(text: String): String {
        instance.apply {
            return try {
                init(Cipher.ENCRYPT_MODE, cryptoKey.toByteArray())
                val encryptedValue = doFinal(text.toByteArray())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Base64.getEncoder().encodeToString(encryptedValue)
                } else {
                    android.util.Base64.encodeToString(encryptedValue, android.util.Base64.DEFAULT)
                }
            } catch (e: Exception) {
                "error"
            }
        }
    }

    fun cipherDecrypt(text: String): String {
        instance.apply {
            return try {
                init(Cipher.DECRYPT_MODE, cryptoKey.toByteArray())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String(doFinal(Base64.getMimeDecoder().decode(text)))
                } else {
                    String(
                        doFinal(android.util.Base64.decode(text, android.util.Base64.DEFAULT))
                    )
                }
            } catch (e: Exception) {
                "error"
            }
        }
    }

    private fun Cipher.init(mode: Int, key: ByteArray) =
        init(mode, SecretKeySpec(key, "AES"), IvParameterSpec(key))

    private val instance: Cipher get() = Cipher.getInstance("AES/CBC/PKCS5Padding")
}