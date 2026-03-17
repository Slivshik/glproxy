<?php
/**
 * WLRUS Secure Crypt Library
 * Enhanced version with environment variable support and secure key loading
 */

class WlrusCrypt {
    
    // Default keys (fallback only - should use env vars in production)
    private static $DEFAULT_KEYS = [
        "wlrus_private_key_01_32bytes_long!!",
        "wlrus_private_key_02_32bytes_long!!",
        "wlrus_private_key_03_32bytes_long!!",
        "wlrus_private_key_04_32bytes_long!!",
        "wlrus_private_key_05_32bytes_long!!"
    ];
    
    // Cached keys loaded from secure source
    private static $loadedKeys = null;
    
    private const CRYPT_PREFIX = "wlrus://crypt/";
    
    /**
     * Load encryption keys from secure source
     * Priority: Environment Variables > Config File > Default Keys
     * @return array Array of 5 private keys
     */
    private static function loadKeys() {
        if (self::$loadedKeys !== null) {
            return self::$loadedKeys;
        }
        
        $keys = [];
        
        // Try to load from environment variables first (most secure)
        $loadedFromEnv = true;
        for ($i = 0; $i < 5; $i++) {
            $envKey = getenv("WLRUS_CRYPT_KEY_$i");
            if ($envKey === false || empty($envKey)) {
                $loadedFromEnv = false;
                break;
            }
            $keys[$i] = $envKey;
        }
        
        if ($loadedFromEnv && count($keys) === 5) {
            self::$loadedKeys = $keys;
            error_log("WlrusCrypt: Loaded keys from environment variables");
            return self::$loadedKeys;
        }
        
        // Try to load from config file
        $configPaths = [
            __DIR__ . '/../keys/crypt_keys_config.php',
            __DIR__ . '/keys/crypt_keys_config.php',
            '/etc/wlrus/crypt_keys_config.php'
        ];
        
        foreach ($configPaths as $configPath) {
            if (file_exists($configPath)) {
                $loadedKeys = include $configPath;
                if (is_array($loadedKeys) && count($loadedKeys) === 5) {
                    self::$loadedKeys = $loadedKeys;
                    error_log("WlrusCrypt: Loaded keys from config file: $configPath");
                    return self::$loadedKeys;
                }
            }
        }
        
        // Fallback to default keys (not recommended for production)
        error_log("WlrusCrypt: WARNING - Using default keys. Set environment variables or config file!");
        self::$loadedKeys = self::$DEFAULT_KEYS;
        return self::$loadedKeys;
    }
    
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
            $keys = self::loadKeys();
            $key = $keys[$keyIndex];
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
            $keys = self::loadKeys();
            foreach ($keys as $index => $key) {
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
     * @return array Array of key fingerprints (first 16 chars of SHA-256 hash)
     */
    public static function getPublicKeys() {
        $keys = self::loadKeys();
        $publicKeys = [];
        foreach ($keys as $key) {
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

?>
