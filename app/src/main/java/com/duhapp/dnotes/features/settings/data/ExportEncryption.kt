package com.duhapp.dnotes.features.settings.data

import android.util.Base64
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object ExportEncryption {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16
    private const val IV_LENGTH = 12 // Standard for GCM
    private const val TAG_LENGTH = 128

    /**
     * PBKDF2 rounds used for new backups. Every backup stores the count it was written
     * with in [BackupEnvelope.kdfIterations], so raising this stays backwards compatible.
     */
    const val ITERATIONS = 120_000

    fun encrypt(data: String, password: CharArray): String {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)

        val secret = deriveKey(password, salt, ITERATIONS)

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

    fun decrypt(
        encryptedBase64: String,
        password: CharArray,
        iterations: Int = ITERATIONS
    ): String {
        val combined = try {
            Base64.decode(encryptedBase64, Base64.NO_WRAP)
        } catch (e: IllegalArgumentException) {
            throw InvalidBackupFileException("Encrypted backup payload is not valid Base64", e)
        }
        if (combined.size <= SALT_LENGTH + IV_LENGTH) {
            throw InvalidBackupFileException("Encrypted backup payload is truncated")
        }

        val salt = combined.sliceArray(0 until SALT_LENGTH)
        val iv = combined.sliceArray(SALT_LENGTH until SALT_LENGTH + IV_LENGTH)
        val encrypted = combined.sliceArray(SALT_LENGTH + IV_LENGTH until combined.size)

        val secret = deriveKey(password, salt, iterations)

        val decrypted = try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secret, GCMParameterSpec(TAG_LENGTH, iv))
            cipher.doFinal(encrypted)
        } catch (e: GeneralSecurityException) {
            // GCM fails authentication for a wrong password just like it does for a
            // tampered file, so both surface as a password problem.
            throw InvalidBackupPasswordException(e)
        }
        return String(decrypted, Charsets.UTF_8)
    }

    private fun deriveKey(password: CharArray, salt: ByteArray, iterations: Int): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(password, salt, iterations, KEY_LENGTH)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }
}
