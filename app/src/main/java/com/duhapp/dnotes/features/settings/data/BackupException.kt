package com.duhapp.dnotes.features.settings.data

/** The selected file is not a DNotes backup, is truncated or was written by a newer build. */
class InvalidBackupFileException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/** The backup is encrypted and the supplied password does not open it. */
class InvalidBackupPasswordException(
    cause: Throwable? = null
) : Exception("Backup could not be decrypted with the given password", cause)
