# Quick Start Guide

## Step 1: Generate Keys

On a server with PHP installed, run:

```bash
cd /path/to/crypto
php generate_keys.php
```

This creates:
- `keys/public_keys.json` - Public fingerprints (safe to share)
- `keys/crypt_keys_config.php` - Server keys (PROTECTED)
- `keys/CryptKeys.kt` - Android app keys (PROTECTED)
- `keys/.env.example` - Environment variable template

## Step 2: Configure Server

### Option A: Environment Variables (Recommended)

Set these in your web server or `.env` file:

```
WLRUS_CRYPT_KEY_0=your_generated_key_0
WLRUS_CRYPT_KEY_1=your_generated_key_1
WLRUS_CRYPT_KEY_2=your_generated_key_2
WLRUS_CRYPT_KEY_3=your_generated_key_3
WLRUS_CRYPT_KEY_4=your_generated_key_4
```

### Option B: Config File

Move `keys/crypt_keys_config.php` outside web root:

```bash
sudo mkdir -p /etc/wlrus
sudo cp keys/crypt_keys_config.php /etc/wlrus/
sudo chmod 600 /etc/wlrus/crypt_keys_config.php
```

## Step 3: Deploy Proxy

Copy proxy files to your web server's API directory:

```bash
cp proxy/*.php /var/www/html/api/
```

## Step 4: Update Android App

Copy `keys/CryptKeys.kt` to your Android project:

```bash
cp keys/CryptKeys.kt /path/to/android/app/src/main/java/com/wlrus/util/
```

## Step 5: Test

### Encrypt a URL:
```bash
curl -X POST https://your-server.com/api/encrypt_api.php \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com/sub","key_index":0}'
```

### Proxy a request:
```bash
curl -X POST https://your-server.com/api/proxy.php \
  -H "Content-Type: application/json" \
  -d '{"crypt_url":"wlrus://crypt/...","method":"GET"}'
```

## Architecture

```
Client -> Your Server (decrypt & proxy) -> Target Server
       [wlrus://crypt/...]              [HTTPS request]
```

The client never sees the actual subscription URL - it only knows the encrypted `wlrus://crypt/...` format.
