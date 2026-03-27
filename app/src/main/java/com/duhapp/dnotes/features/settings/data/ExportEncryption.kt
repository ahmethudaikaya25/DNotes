package com.duhapp.dnotes.features.settings.data

import android.util.Base64
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class ExportEncryption {
    companion object {
        private const val ALGORITHM = "AES/GCM/NoPadding"
        private const val KEY_LENGTH = 256
        private const val ITERATIONS = 10000
        private const val SALT_LENGTH = 16
        private const val IV_LENGTH = 12 // Standard for GCM
        private const val TAG_LENGTH = 128

        fun encrypt(data: String, password: CharArray): String {
            val salt = ByteArray(SALT_LENGTH)
            SecureRandom().nextBytes(salt)

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec: KeySpec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH)
            val tmp = factory.generateSecret(spec)
            val secret = SecretKeySpec(tmp.encoded, "AES")

            val iv = ByteArray(IV_LENGTH)
            SecureRandom().nextBytes(iv)

            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secret, GCMParameterSpec(TAG_LENGTH, iv))

            val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

            // Output format: salt | iv | encryptedData
            val combined = ByteArray(salt.size + iv.size + encrypted.size)
            System.arraycopy(salt, 0, combined, 0, salt.size)
            System.arraycopy(iv, 0, combined, salt.size, iv.size)
            System.arraycopy(encrypted, 0, combined, salt.size + iv.size, encrypted.size)

            return Base64.encodeToString(combined, Base64.NO_WRAP)
        }

        fun decrypt(encryptedBase64: String, password: CharArray): String {
            val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)

            val salt = combined.sliceArray(0 until SALT_LENGTH)
            val iv = combined.sliceArray(SALT_LENGTH until SALT_LENGTH + IV_LENGTH)
            val encrypted = combined.sliceArray(SALT_LENGTH + IV_LENGTH until combined.size)

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec: KeySpec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH)
            val tmp = factory.generateSecret(spec)
            val secret = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secret, GCMParameterSpec(TAG_LENGTH, iv))

            val decrypted = cipher.doFinal(encrypted)
            return String(decrypted, Charsets.UTF_8)
        }
    }
}
