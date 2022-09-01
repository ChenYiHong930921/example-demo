package com.chenyihong.exampledemo.biometrics

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.annotation.RequiresApi
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@RequiresApi(Build.VERSION_CODES.M)
object CryptographyManager {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    private const val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private var ivParameterSpec: IvParameterSpec? = null

    private fun getCipher(): Cipher {
        return Cipher.getInstance("$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING")
    }

    fun getEncryptCipher(keyName: String): Cipher {
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, getOrGenerateSecretKey(keyName))
        ivParameterSpec = cipher.parameters.getParameterSpec(IvParameterSpec::class.java)
        return cipher
    }

    fun getDecryptCipher(keyName: String): Cipher? {
        ivParameterSpec?.let { it ->
            val cipher = getCipher()
            cipher.init(Cipher.DECRYPT_MODE, getOrGenerateSecretKey(keyName), it)
            return cipher
        }
        return null
    }

    fun deleteKey(keyName: String) {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            keyStore.deleteEntry(keyName)
            Log.i(TAG, "delete exits key by keyName:$keyName")
        } catch (e: Exception) {
            Log.i(TAG, "deleteKey error:${e.message}")
            e.printStackTrace()
        }
    }

    private fun getOrGenerateSecretKey(keyName: String): SecretKey {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            keyStore.getKey(keyName, null)?.let {
                Log.i(TAG, "get exits key")
                return it as SecretKey
            }
        } catch (e: Exception) {
            Log.i(TAG, "getSecretKey error:${e.message}")
            e.printStackTrace()
        }
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).run {
            setBlockModes(ENCRYPTION_BLOCK_MODE)
            setEncryptionPaddings(ENCRYPTION_PADDING)
            setUserAuthenticationRequired(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Invalidate the keys if the user has registered a new biometric
                // credential, such as a new fingerprint. Can call this method only
                // on Android 7.0 (API level 24) or higher. The variable
                // "invalidatedByBiometricEnrollment" is true by default.
                setInvalidatedByBiometricEnrollment(false)
            }
            build()
        }
        val keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM, ANDROID_KEYSTORE)
        keyGenerator.init(keyGenParameterSpec)
        Log.i(TAG, "generate new key")
        return keyGenerator.generateKey()
    }
}