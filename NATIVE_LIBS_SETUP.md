# Native Libraries Setup

The Android `.so` files are **not** on the hev-socks5-tunnel GitHub releases page —
those are Linux/macOS/Windows executables. The Android builds ship inside the
official **v2rayNG APK**.

## Quickest Method — Extract from v2rayNG APK

### Step 1 — Download v2rayNG APK
https://github.com/2dust/v2rayNG/releases/latest

Get the universal APK (e.g. `v2rayNG_1.x.x.apk`).

### Step 2 — Run the extraction script (Windows PowerShell)
```powershell
.\extract_libs.ps1  path\to\v2rayNG.apk
```

### Step 2 (alternative) — Python (cross-platform)
```bash
python3 extract_libs.py path/to/v2rayNG.apk
```

Both scripts extract directly into the correct project locations:
```
app/
├── libs/
│   └── libv2ray.aar
└── src/main/jniLibs/
    ├── arm64-v8a/
    │   ├── libhev-socks5-tunnel.so
    │   └── libv2ray.so
    ├── armeabi-v7a/
    │   ├── libhev-socks5-tunnel.so
    │   └── libv2ray.so
    └── x86_64/
        ├── libhev-socks5-tunnel.so
        └── libv2ray.so
```

### Step 3 — Build
```
.\gradlew.bat assemblePlayDebug
```

## Why not the hev-socks5-tunnel GitHub releases?
Those binaries (`hev-socks5-tunnel-linux-arm64` etc.) are standalone CLI tools
for Linux, macOS, FreeBSD, and Windows — **not** Android shared libraries.
The Android `.so` (compiled with the Android NDK, exposing JNI symbols
`TProxyStartService`/`TProxyStopService`/`TProxyGetStats`) is only distributed
inside v2rayNG APKs.
