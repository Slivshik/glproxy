package com.wlrus.util

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.wlrus.AppConfig
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID

/**
 * HWID (Hardware ID) Generator and Manager
 * Generates a unique device identifier and manages it via MMKV
 */
object HwidUtil {

    private const val HWID_STORAGE_KEY = "hwid_storage_key"
    private const val TAG = "HwidUtil"

    /**
     * Get or generate HWID for this device
     * @param context Android context
     * @return HWID string
     */
    fun getOrGenerateHwid(context: Context): String {
        // Try to get from storage first
        val storedHwid = MmkvManager.decodeSettingsString(HWID_STORAGE_KEY)
        if (!storedHwid.isNullOrBlank()) {
            return storedHwid
        }

        // Generate new HWID
        val hwid = generateHwid(context)
        
        // Store it
        MmkvManager.encodeSettings(HWID_STORAGE_KEY, hwid)
        
        Log.i(TAG, "Generated new HWID: $hwid")
        return hwid
    }

    /**
     * Generate HWID from device information
     * Combines Android ID, Serial, Brand, Model, Hardware info into a hash
     */
    private fun generateHwid(context: Context): String {
        try {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: ""

            val brand = android.os.Build.BRAND ?: ""
            val model = android.os.Build.MODEL ?: ""
            val manufacturer = android.os.Build.MANUFACTURER ?: ""
            val hardware = android.os.Build.HARDWARE ?: ""
            val serial = getSerial()

            // Combine all info
            val rawInfo = "$androidId|$brand|$model|$manufacturer|$hardware|$serial"
            
            // Create SHA-256 hash
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(rawInfo.toByteArray(StandardCharsets.UTF_8))
            
            // Convert to hex string
            return digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate HWID", e)
            // Fallback to random UUID
            return UUID.randomUUID().toString().replace("-", "")
        }
    }

    /**
     * Get device serial number (handles different Android versions)
     */
    private fun getSerial(): String {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                android.os.Build.getSerial()
            } else {
                @Suppress("DEPRECATION")
                android.os.Build.SERIAL
            }
        } catch (e: Exception) {
            "unknown"
        }
    }

    /**
     * Get a formatted HWID for display (first 8 chars + ... + last 8 chars)
     */
    fun getFormattedHwid(hwid: String): String {
        return if (hwid.length > 16) {
            "${hwid.substring(0, 8)}...${hwid.substring(hwid.length - 8)}"
        } else {
            hwid
        }
    }

    /**
     * Reset HWID (generates a new one)
     */
    fun resetHwid(context: Context): String {
        MmkvManager.encodeSettings(HWID_STORAGE_KEY, null)
        return getOrGenerateHwid(context)
    }
}
