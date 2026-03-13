# WLRUS — Build & Customization Guide

## What is WLRUS?

WLRUS is a fully rebranded Android VPN client based on v2rayNG.  
It supports VMess, VLESS, Trojan, Shadowsocks, WireGuard, Hysteria2, and more.

---

## Project Structure

```
WLRUS/
├── app/
│   ├── build.gradle.kts              ← App-level Gradle config
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/wlrus/
│   │   │   ├── AppConfig.kt          ← App constants & ports
│   │   │   ├── BuiltinSubscriptions.kt  ← ⭐ DEFAULT SUBS + SIMPLE MODE
│   │   │   ├── WlrusApplication.kt   ← App init, installs default subs
│   │   │   ├── ui/                   ← All Activities
│   │   │   ├── handler/              ← Business logic
│   │   │   ├── service/              ← VPN/proxy services
│   │   │   └── ...
│   │   └── res/
│   │       ├── values/colors.xml     ← ⭐ Design tokens (change accent here)
│   │       ├── values/themes.xml     ← Material theme
│   │       ├── values/strings.xml    ← All text (edit for localization)
│   │       └── layout/               ← XML layouts
├── build.gradle.kts                  ← Root Gradle
├── settings.gradle.kts               ← Module config
└── gradle.properties                 ← JVM / Gradle tuning
```

---

## ⭐ How to Add Default Subscriptions

Open `app/src/main/java/com/wlrus/BuiltinSubscriptions.kt`:

```kotlin
val DEFAULT_BUILTIN_SUBS: List<BuiltinSub> = listOf(
    BuiltinSub(
        name = "My Server Pool",
        url  = "https://your-server.com/sub/token123",
        autoUpdate = true
    ),
    BuiltinSub(
        name = "Backup Servers",
        url  = "https://backup.example.com/sub",
        autoUpdate = false
    ),
)
```

These are automatically installed for **new users** on first launch.  
Existing users with subscriptions are not affected.

---

## ⭐ Simple Mode — Remove Buttons in One Click

In `BuiltinSubscriptions.kt`, change:

```kotlin
const val SIMPLE_MODE = false   // ← change to true
```

This hides: QR scanner, routing settings, logcat, tasker, backup, custom JSON config.  
Perfect for beginner-focused or locked-down builds.

To customize which buttons hide, edit `SimpleMode.HIDE_BUTTONS`:

```kotlin
val HIDE_BUTTONS = if (SIMPLE_MODE) setOf(
    "btn_scan",
    "btn_routing",
    // add or remove entries here
) else emptySet<String>()
```

---

## ⭐ Change the Accent Color

Open `app/src/main/res/values/colors.xml`:

```xml
<!-- Electric cyan (default) -->
<color name="colorAccent">#00D4FF</color>

<!-- Examples of other accents: -->
<!-- Purple:  #A855F7 -->
<!-- Green:   #22C55E -->
<!-- Orange:  #F97316 -->
<!-- Red:     #EF4444 -->
```

All UI elements (buttons, FAB, ping badges, selected server border) derive from `colorAccent` automatically.

---

## 🔨 How to Build on Windows

### Prerequisites

1. **JDK 17** — Download from https://adoptium.net/  
   During install, check "Set JAVA_HOME variable"

2. **Android SDK** — Install via Android Studio  
   https://developer.android.com/studio  
   Or standalone: https://developer.android.com/studio#command-tools

3. **Git** (optional but recommended)  
   https://git-scm.com/download/win

4. **NDK r26** (needed for native .so files)  
   In Android Studio: SDK Manager → SDK Tools → NDK (Side by side)

---

### Step 1 — Set environment variables

Open PowerShell as Administrator:

```powershell
# Set JAVA_HOME (adjust path if different)
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot", "Machine")

# Set ANDROID_HOME
[System.Environment]::SetEnvironmentVariable("ANDROID_HOME", "$env:LOCALAPPDATA\Android\Sdk", "Machine")

# Add to PATH
$path = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
[System.Environment]::SetEnvironmentVariable("Path", "$path;%JAVA_HOME%\bin;%ANDROID_HOME%\platform-tools", "Machine")
```

Close and reopen PowerShell, then verify:

```powershell
java -version     # should say openjdk 17
```

---

### Step 2 — Copy native libraries from original v2rayNG

The VPN core is a native `.so` file. You need to copy it from the original project:

```
v2rayN-master\V2rayNG\app\libs\  →  WLRUS\app\libs\
```

These files should include:
- `arm64-v8a\libv2ray.so`
- `armeabi-v7a\libv2ray.so`  
- `x86_64\libv2ray.so`
- Any `.aar` files

---

### Step 3 — Copy all Kotlin source files

You need to copy and rename the source files from v2rayNG to WLRUS.  
Use the rename script below (PowerShell):

```powershell
# Run from the v2rayNG source root
# This copies all .kt files and replaces package names

$src = "V2rayNG\app\src\main\java\com\v2ray\ang"
$dst = "WLRUS\app\src\main\java\com\wlrus"

# Copy all files
Copy-Item -Recurse $src\* $dst\ -Force

# Replace package declarations in all .kt files
Get-ChildItem -Recurse $dst -Filter "*.kt" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'package com\.v2ray\.ang', 'package com.wlrus'
    $content = $content -replace 'import com\.v2ray\.ang', 'import com.wlrus'
    $content = $content -replace 'com\.v2ray\.ang', 'com.wlrus'
    Set-Content $_.FullName $content -NoNewline
}

Write-Host "Done! Check WLRUS\app\src\main\java\com\wlrus\"
```

> ⚠️ After running, **replace** these files with the versions from this WLRUS package:
> - `AppConfig.kt`
> - `BuiltinSubscriptions.kt`  
> - `WlrusApplication.kt` (replaces `AngApplication.kt`)

---

### Step 4 — Copy and patch resource files

```powershell
# Copy res folder from v2rayNG
$srcRes = "V2rayNG\app\src\main\res"
$dstRes = "WLRUS\app\src\main\res"

Copy-Item -Recurse $srcRes\* $dstRes\ -Force

# Then overwrite with WLRUS custom resources (already in this package):
# - res\values\colors.xml      ← WLRUS dark theme
# - res\values\themes.xml      ← WLRUS material theme
# - res\values\strings.xml     ← WLRUS branding
# - res\layout\activity_main.xml
# - res\layout\nav_header.xml
# - res\layout\item_recycler_main.xml
# - res\layout\activity_sub_edit.xml
# - res\menu\menu_drawer.xml
```

---

### Step 5 — Build the APK

```powershell
cd WLRUS

# Debug build (fastest, good for testing)
.\gradlew assemblePlayDebug

# Release build (requires signing config)
.\gradlew assemblePlayRelease
```

The APK will appear in:
```
WLRUS\app\build\outputs\apk\play\debug\app-play-debug.apk
```

---

### Step 6 — Install on device

```powershell
# Make sure USB debugging is enabled on your phone
adb install app\build\outputs\apk\play\debug\app-play-debug.apk
```

---

## 🔑 Signing a Release Build

1. Generate a keystore (do this once):
```powershell
keytool -genkey -v -keystore wlrus.jks -keyalg RSA -keysize 2048 -validity 10000 -alias wlrus
```

2. Edit `app/build.gradle.kts`, uncomment and fill in `signingConfigs.release`:
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("wlrus.jks")
        storePassword = "yourpassword"
        keyAlias = "wlrus"
        keyPassword = "yourkeypassword"
    }
}
```

3. Build:
```powershell
.\gradlew assemblePlayRelease
```

> ⚠️ Never commit `wlrus.jks` or passwords to git. Use environment variables in CI.

---

## Common Build Errors

| Error | Fix |
|-------|-----|
| `SDK location not found` | Set `ANDROID_HOME` env var, or create `local.properties` with `sdk.dir=C\:\\Users\\you\\AppData\\Local\\Android\\Sdk` |
| `Unsupported class file major version` | You need JDK 17. Run `java -version` to check |
| `libv2ray.so not found` | Copy the `libs/` folder from original v2rayNG |
| `Duplicate class` | Make sure you deleted old `AngApplication.kt` after adding `WlrusApplication.kt` |
| `Could not resolve dependency` | Check your internet connection; Gradle downloads from Maven Central |
| `Out of memory` | Increase in `gradle.properties`: `org.gradle.jvmargs=-Xmx6g` |

---

## Renaming Summary

| Old (v2rayNG) | New (WLRUS) |
|---------------|-------------|
| `com.v2ray.ang` | `com.wlrus` |
| `V2rayNG` | `WLRUS` |
| `AngApplication` | `WlrusApplication` |
| `AppConfig.ANG_PACKAGE` | `AppConfig.ANG_PACKAGE = "com.wlrus"` |
| `v2rayN-master/V2rayNG/` | `WLRUS/` |
| App label | "WLRUS" |
| Channel ID | "wlrus_channel" |
| Broadcast actions | `com.wlrus.action.*` |

---

*WLRUS is based on v2rayNG (Apache 2.0). All original license terms apply.*
