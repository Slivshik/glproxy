<?php
/**
 * WLRUS Crypt API - PHP Library
 * For encrypting subscription URLs and content using wlrus://crypt/ format
 * Compatible with Android app's CryptUtil
 */

class WlrusCrypt {
    
    // Five private keys (must match exactly with Android app)
    private static $PRIVATE_KEYS = [
        "wlrus_private_key_01_32bytes_long!!",
        "wlrus_private_key_02_32bytes_long!!",
        "wlrus_private_key_03_32bytes_long!!",
        "wlrus_private_key_04_32bytes_long!!",
        "wlrus_private_key_05_32bytes_long!!"
    ];
    
    private const CRYPT_PREFIX = "wlrus://crypt/";
    
    /**
     * Encrypt a string using AES-256-CBC
     * @param string $plainText The text to encrypt
     * @param int $keyIndex Which key to use (0-4), default is 0
     * @return string|false Encrypted string in wlrus://crypt/*encrypted* format or false on failure
     */
    public static function encrypt($plainText, $keyIndex = 0) {
        if (!is_int($keyIndex) || $keyIndex < 0 || $keyIndex > 4) {
            error_log("Invalid key index: $keyIndex");
            return false;
        }
        
        try {
            $key = self::$PRIVATE_KEYS[$keyIndex];
            $keyBytes = hash('sha256', $key, true); // 32 bytes
            
            // Zero IV (16 bytes)
            $iv = str_repeat("\0", 16);
            
            // Pad the plaintext using PKCS7
            $blockSize = 16;
            $paddingLen = $blockSize - (strlen($plainText) % $blockSize);
            $paddedPlainText = $plainText . str_repeat(chr($paddingLen), $paddingLen);
            
            // Encrypt using AES-256-CBC
            $encrypted = openssl_encrypt(
                $paddedPlainText,
                'AES-256-CBC',
                $keyBytes,
                OPENSSL_RAW_DATA | OPENSSL_ZERO_PADDING,
                $iv
            );
            
            if ($encrypted === false) {
                error_log("Encryption failed: " . openssl_error_string());
                return false;
            }
            
            // Encode to Base64
            $base64Encoded = base64_encode($encrypted);
            
            return self::CRYPT_PREFIX . $base64Encoded;
            
        } catch (Exception $e) {
            error_log("Encryption error: " . $e->getMessage());
            return false;
        }
    }
    
    /**
     * Decrypt a wlrus://crypt/*encrypted* string
     * Tries all 5 private keys until one works
     * @param string $cryptString The encrypted string
     * @return string|false Decrypted string or false on failure
     */
    public static function decrypt($cryptString) {
        if (!self::isCryptUrl($cryptString)) {
            return false;
        }
        
        try {
            // Extract the encrypted part after wlrus://crypt/
            $encryptedPart = substr($cryptString, strlen(self::CRYPT_PREFIX));
            
            if (empty($encryptedPart)) {
                error_log("Empty encrypted content");
                return false;
            }
            
            // Decode from Base64
            $encryptedBytes = base64_decode($encryptedPart, true);
            
            if ($encryptedBytes === false || empty($encryptedBytes)) {
                error_log("Failed to decode Base64");
                return false;
            }
            
            // Try each private key
            foreach (self::$PRIVATE_KEYS as $index => $key) {
                $decrypted = self::decryptWithKey($encryptedBytes, $key, $index);
                if ($decrypted !== false) {
                    error_log("Successfully decrypted with key index: $index");
                    return $decrypted;
                }
            }
            
            error_log("All keys failed to decrypt");
            return false;
            
        } catch (Exception $e) {
            error_log("Decryption error: " . $e->getMessage());
            return false;
        }
    }
    
    /**
     * Check if a string is an encrypted wlrus crypt URL
     * @param string|null $str The string to check
     * @return bool True if it's a crypt URL
     */
    public static function isCryptUrl($str) {
        return is_string($str) && strpos($str, self::CRYPT_PREFIX) === 0;
    }
    
    /**
     * Get public key identifiers (for documentation/debugging)
     * @return array Array of key identifiers (first 16 chars of SHA-256 hash)
     */
    public static function getPublicKeys() {
        $publicKeys = [];
        foreach (self::$PRIVATE_KEYS as $key) {
            $hash = hash('sha256', $key);
            $publicKeys[] = substr($hash, 0, 16);
        }
        return $publicKeys;
    }
    
    /**
     * Encrypt a subscription URL
     * @param string $url The subscription URL to encrypt
     * @param int $keyIndex Which key to use (0-4)
     * @return string|false Encrypted URL or false on failure
     */
    public static function encryptSubscriptionUrl($url, $keyIndex = 0) {
        return self::encrypt($url, $keyIndex);
    }
    
    /**
     * Encrypt subscription content (the actual config data)
     * @param string $content The subscription content to encrypt
     * @param int $keyIndex Which key to use (0-4)
     * @return string|false Encrypted content or false on failure
     */
    public static function encryptSubscriptionContent($content, $keyIndex = 0) {
        return self::encrypt($content, $keyIndex);
    }
    
    /**
     * Generate a secure random key (for creating new key sets)
     * @return string A 32-character random string
     */
    public static function generateRandomKey() {
        return bin2hex(random_bytes(16));
    }
    
    /**
     * Decrypt with a specific key
     * @param string $encryptedBytes The encrypted data
     * @param string $keyString The key string
     * @param int $index Key index for logging
     * @return string|false Decrypted string or false on failure
     */
    private static function decryptWithKey($encryptedBytes, $keyString, $index) {
        try {
            $keyBytes = hash('sha256', $keyString, true); // 32 bytes
            
            // Zero IV (16 bytes)
            $iv = str_repeat("\0", 16);
            
            // Decrypt using AES-256-CBC
            $decrypted = openssl_decrypt(
                $encryptedBytes,
                'AES-256-CBC',
                $keyBytes,
                OPENSSL_RAW_DATA | OPENSSL_ZERO_PADDING,
                $iv
            );
            
            if ($decrypted === false) {
                return false;
            }
            
            // Remove PKCS7 padding
            $paddingLen = ord(substr($decrypted, -1));
            if ($paddingLen > 0 && $paddingLen <= 16) {
                $decrypted = substr($decrypted, 0, -$paddingLen);
            }
            
            return $decrypted;
            
        } catch (Exception $e) {
            return false;
        }
    }
}

// Example usage:
// $url = "https://example.com/subscription";
// $encrypted = WlrusCrypt::encrypt($url, 0);
// echo "Encrypted: $encrypted\n";
// 
// $decrypted = WlrusCrypt::decrypt($encrypted);
// echo "Decrypted: $decrypted\n";

?>
