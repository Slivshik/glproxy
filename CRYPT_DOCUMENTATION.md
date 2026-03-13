# WLRUS Crypt System Documentation

## Overview

The WLRUS Crypt system provides encrypted subscription URLs and content, similar to Happ's crypt system. It uses AES-256-CBC encryption with 5 private keys for decryption.

## Format

Encrypted content uses the format: `wlrus://crypt/*base64_encoded_encrypted_data*`

## Features

### 1. HWID Generation and Header Support

- **HWID (Hardware ID)**: Automatically generated unique device identifier
- **Happ-style Headers**: When fetching subscriptions, the following headers are sent:
  - `x-hwid`: Device hardware ID
  - `x-device-model`: Device model (e.g., "Samsung/SM-G998B")
  - `x-device-locale`: Device locale (e.g., "en_US")
  - `x-ver-os`: Android version (e.g., "Android 13")
  - `User-agent`: Configurable (WLRUS, Happ-style, or custom)

### 2. User Agent Modes

Three modes are supported:
- **WLRUS**: Default user agent (`WLRUS/{version}`)
- **HAPP**: Happ-style user agent (`Happ/3.10.0`)
- **CUSTOM**: Custom user agent specified in subscription settings

### 3. Encryption/Decryption

#### Android App (Kotlin)

The app automatically:
- Detects `wlrus://crypt/` URLs
- Decrypts them using one of 5 private keys
- Sends Happ-style headers when fetching subscriptions
- Handles encrypted responses from servers

#### PHP API

Use the provided PHP library to encrypt subscription URLs:

```php
require_once 'wlrus_crypt.php';

// Encrypt a subscription URL
$url = "https://example.com/subscription";
$encrypted = WlrusCrypt::encryptSubscriptionUrl($url, 0);
echo $encrypted; // Outputs: wlrus://crypt/base64data...

// Decrypt (if needed on server side)
$decrypted = WlrusCrypt::decrypt($encrypted);
echo $decrypted; // Outputs: https://example.com/subscription
```

### 4. REST API Endpoint

An example REST API endpoint is provided at `/api/encrypt_subscription.php`

**Request:**
```bash
POST /api/encrypt_subscription.php
Content-Type: application/json

{
    "url": "https://example.com/subscription",
    "key_index": 0
}
```

**Response:**
```json
{
    "success": true,
    "encrypted_url": "wlrus://crypt/base64encodeddata...",
    "key_index_used": 0,
    "message": "URL encrypted successfully..."
}
```

## Usage in Subscription Settings

### Setting Custom User Agent

In the subscription edit screen, you can set:
- **User Agent**: Leave blank for default, or enter custom value
  - If starts with "Happ/", uses Happ mode
  - Otherwise uses CUSTOM mode with your value

### Using Encrypted URLs

Simply paste a `wlrus://crypt/...` URL as the subscription URL. The app will:
1. Detect it's encrypted
2. Decrypt it automatically
3. Fetch the actual subscription content
4. Parse and import the configurations

## Security Notes

1. **Key Management**: The 5 private keys are embedded in both the Android app and PHP library. For production use, consider:
   - Using secure key storage (Android Keystore)
   - Rotating keys periodically
   - Using different key sets for different distributions

2. **Encryption Strength**: Uses AES-256-CBC with SHA-256 derived keys

3. **HWID Privacy**: HWID is generated from device information and stored locally. It's only sent to subscription servers that support Happ-style headers.

## File Structure

```
/workspace/
├── app/src/main/java/com/wlrus/util/
│   ├── HwidUtil.kt          # HWID generation and management
│   ├── CryptUtil.kt         # Encryption/decryption utility
│   └── HttpUtil.kt          # HTTP client with Happ headers support
├── app/src/main/java/com/wlrus/handler/
│   └── AngConfigManager.kt  # Updated to support crypt and headers
└── api/
    ├── wlrus_crypt.php              # PHP encryption library
    └── encrypt_subscription.php     # REST API example
```

## Testing

### Test Encryption/Decryption

**PHP:**
```php
<?php
require_once 'wlrus_crypt.php';

$testUrl = "https://example.com/test-subscription";
$encrypted = WlrusCrypt::encrypt($testUrl, 0);
echo "Encrypted: $encrypted\n";

$decrypted = WlrusCrypt::decrypt($encrypted);
echo "Decrypted: $decrypted\n";
echo "Match: " . ($testUrl === $decrypted ? "YES" : "NO") . "\n";
?>
```

**Android (in app):**
```kotlin
val testUrl = "https://example.com/test-subscription"
val encrypted = CryptUtil.encrypt(testUrl)
println("Encrypted: $encrypted")

val decrypted = CryptUtil.decrypt(encrypted!!)
println("Decrypted: $decrypted")
println("Match: ${testUrl == decrypted}")
```

## Migration from Existing Subscriptions

Existing non-encrypted subscriptions continue to work normally. To migrate:

1. Use the PHP API to encrypt your subscription URL
2. Update the subscription URL in the app to the encrypted version
3. Optionally configure User Agent for Happ compatibility

## Troubleshooting

### Decryption Fails
- Ensure the URL starts with `wlrus://crypt/`
- Check that the Base64 data is valid
- Verify keys match between PHP and Android

### Headers Not Sent
- Headers are automatically added when no custom User Agent is set
- Custom User Agent in subscription settings overrides automatic headers

### HWID Changes
- HWID is persistent across app restarts
- To reset: Clear app data or use developer options (if implemented)
