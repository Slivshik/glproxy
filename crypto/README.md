# WLRUS Crypt System - Secure Key Generation and Proxy Guide

## Overview

This guide explains how to generate secure encryption keys, store them safely, and proxy all requests through your PHP server.

**Architecture Flow:**
```
Client -> Your Server (decrypt & forward) -> Target Subscription Server
       ^                                    ^
       |                                    |
   Encrypted URL                      Actual Request
   wlrus://crypt/...                  with headers
```

## Quick Start

### 1. Generate Encryption Keys

Run the key generator script:

```bash
cd /workspace/crypto
php generate_keys.php
```

This will create:
- `keys/public_keys.json` - Safe to share (public fingerprints only)
- `keys/crypt_keys_config.php` - **PROTECTED** - Server-side keys
- `keys/CryptKeys.kt` - **PROTECTED** - Android app keys
- `keys/.env.example` - Environment variable template
- `keys/key_XX.enc.json` - Individual encrypted key files

### 2. Configure Server Security

#### Option A: Environment Variables (Recommended)

Copy the example env file and set environment variables:

```bash
cp keys/.env.example .env
# Edit .env with your actual keys
```

In your web server configuration, load these as environment variables:

**Apache (.htaccess or virtual host):**
```apache
SetEnv WLRUS_CRYPT_KEY_0 "your_key_0_here"
SetEnv WLRUS_CRYPT_KEY_1 "your_key_1_here"
SetEnv WLRUS_CRYPT_KEY_2 "your_key_2_here"
SetEnv WLRUS_CRYPT_KEY_3 "your_key_3_here"
SetEnv WLRUS_CRYPT_KEY_4 "your_key_4_here"
```

**Nginx (with PHP-FPM):**
```nginx
location ~ \.php$ {
    fastcgi_param WLRUS_CRYPT_KEY_0 "your_key_0_here";
    fastcgi_param WLRUS_CRYPT_KEY_1 "your_key_1_here";
    # ... etc
}
```

#### Option B: Config File

Move the config file outside web root:

```bash
sudo mkdir -p /etc/wlrus
sudo cp keys/crypt_keys_config.php /etc/wlrus/
sudo chmod 600 /etc/wlrus/crypt_keys_config.php
```

The library automatically looks for keys in `/etc/wlrus/crypt_keys_config.php`.

### 3. Deploy Proxy Server

Copy proxy files to your web server:

```bash
# Copy to your web root
cp -r proxy/* /var/www/html/api/

# Set proper permissions
chmod 600 /var/www/html/api/wlrus_crypt_secure.php
chmod 644 /var/www/html/api/proxy.php
chmod 644 /var/www/html/api/encrypt_api.php
```

### 4. Update Android App

Replace the keys in your Android app with the newly generated ones:

1. Copy `keys/CryptKeys.kt` to `app/src/main/java/com/wlrus/util/`
2. Update imports in `CryptUtil.kt` if needed
3. Rebuild the app

## API Endpoints

### Encrypt URL Endpoint

**POST** `/api/encrypt_api.php`

**Request:**
```json
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
    "key_fingerprint": "abc123def456789",
    "message": "URL encrypted successfully..."
}
```

### Proxy Endpoint

**POST** `/api/proxy.php`

**Request:**
```json
{
    "crypt_url": "wlrus://crypt/base64encodeddata...",
    "method": "GET",
    "headers": {
        "User-Agent": "WLRUS/1.0",
        "x-hwid": "device-hardware-id",
        "x-device-model": "Samsung/SM-G998B"
    },
    "post_data": null
}
```

**Response:**
```json
{
    "success": true,
    "http_code": 200,
    "data": "<actual subscription content>",
    "original_url": "https://example.com/subscription",
    "message": "Request proxied successfully"
}
```

## Client Implementation Example

### JavaScript/TypeScript Client

```typescript
class WlrusClient {
    private proxyUrl: string;
    
    constructor(proxyUrl: string) {
        this.proxyUrl = proxyUrl;
    }
    
    async fetchSubscription(cryptUrl: string, headers?: Record<string, string>): Promise<string> {
        const response = await fetch(this.proxyUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                crypt_url: cryptUrl,
                method: 'GET',
                headers: headers || {}
            })
        });
        
        const result = await response.json();
        
        if (!result.success) {
            throw new Error(result.error);
        }
        
        return result.data;
    }
}

// Usage
const client = new WlrusClient('https://your-server.com/api/proxy.php');

const cryptUrl = 'wlrus://crypt/...';
const subscriptionData = await client.fetchSubscription(cryptUrl, {
    'x-hwid': 'your-device-id',
    'x-device-model': 'Custom/Device'
});

console.log(subscriptionData);
```

### cURL Example

```bash
# Encrypt a URL
curl -X POST https://your-server.com/api/encrypt_api.php \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com/sub","key_index":0}'

# Proxy a request
curl -X POST https://your-server.com/api/proxy.php \
  -H "Content-Type: application/json" \
  -d '{
    "crypt_url": "wlrus://crypt/...",
    "method": "GET",
    "headers": {"User-Agent": "WLRUS/1.0"}
  }'
```

## Security Best Practices

### 1. Key Storage

✅ **DO:**
- Use environment variables in production
- Store config files outside web root
- Set restrictive file permissions (chmod 600)
- Use different keys for dev/staging/production
- Rotate keys periodically
- Back up keys securely (encrypted backup)

❌ **DON'T:**
- Commit keys to version control
- Store keys in database without encryption
- Share private keys publicly
- Use default keys in production

### 2. Network Security

✅ **DO:**
- Use HTTPS for all endpoints
- Validate and sanitize all inputs
- Implement rate limiting
- Log all proxy requests
- Set CORS headers appropriately
- Use authentication tokens for API access

❌ **DON'T:**
- Allow arbitrary URL proxying without validation
- Expose internal server details
- Skip SSL verification
- Allow unlimited request sizes

### 3. .gitignore Configuration

Add these to your `.gitignore`:

```gitignore
# Crypt keys - NEVER commit
crypto/keys/*.enc.json
crypto/keys/crypt_keys_config.php
crypto/keys/CryptKeys.kt
crypto/keys/.env
.env

# Keep only templates
!crypto/keys/.env.example
```

### 4. File Permissions

```bash
# Keys directory
chmod 700 crypto/keys/

# Key files
chmod 600 crypto/keys/*.enc.json
chmod 600 crypto/keys/crypt_keys_config.php
chmod 600 crypto/keys/CryptKeys.kt

# Public info
chmod 644 crypto/keys/public_keys.json
chmod 644 crypto/keys/.env.example

# Proxy files
chmod 600 proxy/wlrus_crypt_secure.php
chmod 644 proxy/proxy.php
chmod 644 proxy/encrypt_api.php
```

## Testing

### Test Key Generation

```bash
cd /workspace/crypto
php generate_keys.php
cat keys/public_keys.json
```

### Test Encryption

```bash
curl -X POST http://localhost/api/encrypt_api.php \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com/test","key_index":0}'
```

### Test Proxy

```bash
curl -X POST http://localhost/api/proxy.php \
  -H "Content-Type: application/json" \
  -d '{
    "crypt_url": "wlrus://crypt/[encrypted_url_from_above]",
    "method": "GET"
  }'
```

## Troubleshooting

### Decryption Fails

1. Verify keys match between server and Android app
2. Check environment variables are loaded correctly
3. Ensure config file is readable by web server
4. Check error logs: `tail -f /var/log/apache2/error.log`

### Proxy Returns Errors

1. Verify target URL is accessible from server
2. Check cURL is enabled in PHP
3. Verify SSL certificates are valid
4. Check firewall rules allow outbound connections

### Permission Denied

```bash
# Fix ownership
chown -R www-data:www-data /var/www/html/api/

# Fix permissions
find /var/www/html/api/ -type f -exec chmod 644 {} \;
find /var/www/html/api/ -type d -exec chmod 755 {} \;
chmod 600 /var/www/html/api/wlrus_crypt_secure.php
```

## Migration from Existing Setup

If you're migrating from the old `api/wlrus_crypt.php`:

1. Generate new keys with `generate_keys.php`
2. Update server to use `proxy/wlrus_crypt_secure.php`
3. Update Android app with new `CryptKeys.kt`
4. Test encryption/decryption on both sides
5. Gradually roll out to users

## Support

For issues or questions:
1. Check error logs first
2. Verify key fingerprints match
3. Test with simple URLs before complex subscriptions
4. Ensure PHP OpenSSL extension is enabled
