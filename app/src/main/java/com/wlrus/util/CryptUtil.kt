package com.wlrus.util

import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Crypt utility for wlrus://crypt/*encrypted* format
 * Supports AES-256-CBC encryption/decryption with 5 private keys
 * Similar to Happ's crypt system
 */
object CryptUtil {

    private const val TAG = "CryptUtil"
    private const val CRYPT_PREFIX = "wlrus://crypt/"
    
    // Five private keys for decryption (in production, these should be securely stored)
    // These are example keys - in real implementation, use secure key management
    private val PRIVATE_KEYS = arrayOf(
        "wlrus_private_key_01_32bytes_long!!",
        "wlrus_private_key_02_32bytes_long!!",
        "wlrus_private_key_03_32bytes_long!!",
        "wlrus_private_key_04_32bytes_long!!",
        "wlrus_private_key_05_32bytes_long!!"
    )

    /**
     * Check if a string is an encrypted wlrus crypt URL
     */
    fun isCryptUrl(str: String?): Boolean {
        return str != null && str.startsWith(CRYPT_PREFIX)
    }

    /**
     * Decrypt a wlrus://crypt/*encrypted* string
     * Tries all 5 private keys until one works
     * @param cryptString The encrypted string starting with wlrus://crypt/
     * @return Decrypted string or null if decryption fails
     */
    fun decrypt(cryptString: String): String? {
        try {
            if (!isCryptUrl(cryptString)) {
                return null
            }

            // Extract the encrypted part after wlrus://crypt/
            val encryptedPart = cryptString.substring(CRYPT_PREFIX.length)
            
            if (encryptedPart.isBlank()) {
                Log.e(TAG, "Empty encrypted content")
                return null
            }

            // Decode from Base64
            val encryptedBytes = Base64.decode(encryptedPart, Base64.DEFAULT)
            
            if (encryptedBytes.isEmpty()) {
                Log.e(TAG, "Failed to decode Base64")
                return null
            }

            // Try each private key
            for ((index, key) in PRIVATE_KEYS.withIndex()) {
                try {
                    val decrypted = decryptWithKey(encryptedBytes, key)
                    if (decrypted != null) {
                        Log.i(TAG, "Successfully decrypted with key index: $index")
                        return decrypted
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Key index $index failed: ${e.message}")
                    // Continue to next key
                }
            }

            Log.e(TAG, "All keys failed to decrypt")
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Decryption error", e)
            return null
        }
    }

    /**
     * Encrypt a string using the primary private key
     * @param plainText The text to encrypt
     * @return Encrypted string in format wlrus://crypt/*encrypted*
     */
    fun encrypt(plainText: String): String? {
        try {
            // Use the first key for encryption
            val key = PRIVATE_KEYS[0]
            
            val encryptedBytes = encryptWithKey(plainText.toByteArray(StandardCharsets.UTF_8), key)
                ?: return null

            val encoded = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
                .replace("\n", "")
            
            return "$CRYPT_PREFIX$encoded"
        } catch (e: Exception) {
            Log.e(TAG, "Encryption error", e)
            return null
        }
    }

    /**
     * Encrypt using a specific key index (for PHP API compatibility)
     * @param plainText The text to encrypt
     * @param keyIndex Index of the key to use (0-4)
     * @return Encrypted string in format wlrus://crypt/*encrypted*
     */
    fun encryptWithKeyIndex(plainText: String, keyIndex: Int): String? {
        try {
            if (keyIndex !in 0..4) {
                Log.e(TAG, "Invalid key index: $keyIndex")
                return null
            }

            val key = PRIVATE_KEYS[keyIndex]
            
            val encryptedBytes = encryptWithKey(plainText.toByteArray(StandardCharsets.UTF_8), key)
                ?: return null

            val encoded = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
                .replace("\n", "")
            
            return "$CRYPT_PREFIX$encoded"
        } catch (e: Exception) {
            Log.e(TAG, "Encryption error", e)
            return null
        }
    }

    /**
     * Decrypt with a specific key
     */
    private fun decryptWithKey(encryptedBytes: ByteArray, keyString: String): String? {
        try {
            val keySpec = generateKeySpec(keyString)
            val iv = ByteArray(16) { 0 } // Zero IV for simplicity
            val ivSpec = IvParameterSpec(iv)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            Log.d(TAG, "Decryption with key failed: ${e.message}")
            return null
        }
    }

    /**
     * Encrypt with a specific key
     */
    private fun encryptWithKey(data: ByteArray, keyString: String): ByteArray? {
        try {
            val keySpec = generateKeySpec(keyString)
            val iv = ByteArray(16) { 0 } // Zero IV for simplicity
            val ivSpec = IvParameterSpec(iv)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

            return cipher.doFinal(data)
        } catch (e: Exception) {
            Log.e(TAG, "Encryption failed: ${e.message}")
            return null
        }
    }

    /**
     * Generate SecretKeySpec from key string (SHA-256 hash to get 32 bytes)
     */
    private fun generateKeySpec(keyString: String): SecretKeySpec {
        val md = MessageDigest.getInstance("SHA-256")
        val keyBytes = md.digest(keyString.toByteArray(StandardCharsets.UTF_8))
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Get all public keys (for PHP API to know which keys to use)
     * Returns hashed public identifiers of the keys
     */
    fun getPublicKeys(): List<String> {
        return PRIVATE_KEYS.map { key ->
            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(key.toByteArray(StandardCharsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }.substring(0, 16)
        }
    }

    /**
     * Strip the wlrus://crypt/ prefix to get raw encrypted data
     */
    fun stripPrefix(cryptString: String): String? {
        return if (isCryptUrl(cryptString)) {
            cryptString.substring(CRYPT_PREFIX.length)
        } else {
            null
        }
    }
}
