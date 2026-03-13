# ============================================================
#  WLRUS Migration Script  v4
#
#  Fixed: downloads libv2ray.aar from AndroidLibXrayLite
#         (not a zip - it's a direct .aar file)
#
#  Usage:
#    1. Extract v2rayN-master.rar so you have v2rayN-master\ folder
#    2. Place WLRUS\ folder in the same directory as this script
#    3. Open PowerShell in that directory
#    4. Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
#    5. .\rename_to_wlrus.ps1
# ============================================================

param(
    [string]$V2raySrc   = ".\v2rayN-master\V2rayNG",
    [string]$WlrusDst   = ".\WLRUS",
    [bool]$DownloadLibs = $true
)

$ErrorActionPreference = "Continue"

# ── All functions defined first ────────────────────────────────────────────────

function Show-ManualLibsInstructions {
    param([string]$dstLibs)
    Write-Host ""
    Write-Host "  HOW TO GET libv2ray.aar MANUALLY:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "  1. Open in browser:" -ForegroundColor White
    Write-Host "     https://github.com/2dust/AndroidLibXrayLite/releases/latest" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  2. Download:  libv2ray.aar  (~52 MB)" -ForegroundColor White
    Write-Host ""
    Write-Host "  3. Copy it to:" -ForegroundColor White
    Write-Host "     $dstLibs\libv2ray.aar" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  Then run:  .\gradlew assemblePlayDebug" -ForegroundColor White
    Write-Host ""
}

function Download-File {
    param([string]$Url, [string]$Dest)

    # Method 1: Invoke-WebRequest
    try {
        Invoke-WebRequest -Uri $Url -OutFile $Dest -UseBasicParsing `
            -TimeoutSec 300 -UserAgent "Mozilla/5.0 WLRUS-build"
        if ((Test-Path $Dest) -and (Get-Item $Dest).Length -gt 10000) {
            return $true
        }
    } catch {
        Write-Host "  Invoke-WebRequest failed: $($_.Exception.Message)" -ForegroundColor DarkGray
    }

    # Method 2: WebClient
    try {
        $wc = New-Object System.Net.WebClient
        $wc.Headers.Add("User-Agent", "Mozilla/5.0 WLRUS-build")
        $wc.DownloadFile($Url, $Dest)
        if ((Test-Path $Dest) -and (Get-Item $Dest).Length -gt 10000) {
            return $true
        }
    } catch {
        Write-Host "  WebClient failed: $($_.Exception.Message)" -ForegroundColor DarkGray
    }

    # Method 3: curl.exe (built into Windows 10+)
    try {
        $result = & curl.exe -L -o $Dest $Url --max-time 300 --silent --show-error 2>&1
        if ((Test-Path $Dest) -and (Get-Item $Dest).Length -gt 10000) {
            return $true
        }
    } catch {
        Write-Host "  curl.exe failed: $($_.Exception.Message)" -ForegroundColor DarkGray
    }

    return $false
}

function Get-LibvrayAar {
    param([string]$dstLibs)

    $destFile = "$dstLibs\libv2ray.aar"

    # Known URLs for libv2ray.aar from AndroidLibXrayLite
    # "latest" redirect works on GitHub even without API access
    $urls = @(
        "https://github.com/2dust/AndroidLibXrayLite/releases/latest/download/libv2ray.aar",
        "https://github.com/2dust/AndroidLibXrayLite/releases/download/25.3.10/libv2ray.aar",
        "https://github.com/2dust/AndroidLibXrayLite/releases/download/25.2.28/libv2ray.aar",
        "https://github.com/2dust/AndroidLibXrayLite/releases/download/25.1.31/libv2ray.aar",
        "https://github.com/2dust/AndroidLibXrayLite/releases/download/24.12.31/libv2ray.aar"
    )

    foreach ($url in $urls) {
        Write-Host "  Trying: $url" -ForegroundColor Gray
        if (Test-Path $destFile) { Remove-Item $destFile -Force }

        $ok = Download-File -Url $url -Dest $destFile
        if ($ok) {
            $sizeMB = [math]::Round((Get-Item $destFile).Length / 1MB, 1)
            Write-Host "  OK - Downloaded libv2ray.aar ($sizeMB MB)" -ForegroundColor Green
            return $true
        }
    }

    return $false
}

# ═════════════════════════════════════════════════════════════════════════════
#  MAIN
# ═════════════════════════════════════════════════════════════════════════════

Write-Host ""
Write-Host "  WLRUS Migration Script v4" -ForegroundColor Cyan
Write-Host "  =========================" -ForegroundColor Cyan
Write-Host ""

if (-not (Test-Path $V2raySrc)) {
    Write-Host "ERROR: Not found: $V2raySrc" -ForegroundColor Red
    Write-Host "Extract v2rayN-master.rar first." -ForegroundColor Yellow
    exit 1
}
if (-not (Test-Path $WlrusDst)) {
    Write-Host "ERROR: Not found: $WlrusDst" -ForegroundColor Red
    exit 1
}

# ── 1. Copy Kotlin sources ────────────────────────────────────────────────────
Write-Host "[1/6] Copying Kotlin source files..." -ForegroundColor Yellow

$srcKt = "$V2raySrc\app\src\main\java\com\v2ray\ang"
$dstKt = "$WlrusDst\app\src\main\java\com\wlrus"

if (-not (Test-Path $srcKt)) {
    Write-Host "  ERROR: $srcKt not found" -ForegroundColor Red; exit 1
}

New-Item -ItemType Directory -Path $dstKt -Force | Out-Null
Copy-Item -Recurse "$srcKt\*" "$dstKt\" -Force
$ktCount = (Get-ChildItem -Recurse $dstKt -Filter "*.kt").Count
Write-Host "  OK - $ktCount .kt files copied" -ForegroundColor Green

# ── 2. Rename packages ────────────────────────────────────────────────────────
Write-Host "[2/6] Rewriting com.v2ray.ang -> com.wlrus..." -ForegroundColor Yellow

$patched = 0
foreach ($file in (Get-ChildItem -Recurse $dstKt -Filter "*.kt")) {
    $raw = [System.IO.File]::ReadAllText($file.FullName, [System.Text.Encoding]::UTF8)
    $new = $raw `
        -replace 'package com\.v2ray\.ang', 'package com.wlrus' `
        -replace 'import com\.v2ray\.ang', 'import com.wlrus' `
        -replace '"com\.v2ray\.ang"', '"com.wlrus"' `
        -replace 'com\.v2ray\.ang\.', 'com.wlrus.'
    if ($raw -ne $new) {
        [System.IO.File]::WriteAllText($file.FullName, $new, [System.Text.Encoding]::UTF8)
        $patched++
    }
}
Write-Host "  OK - Patched $patched files" -ForegroundColor Green

# ── 3. Install WLRUS custom files ─────────────────────────────────────────────
Write-Host "[3/6] Installing WLRUS custom files..." -ForegroundColor Yellow

$oldApp = "$dstKt\AngApplication.kt"
if (Test-Path $oldApp) { Remove-Item $oldApp -Force }
Write-Host "  OK" -ForegroundColor Green

# ── 4. Merge resources ────────────────────────────────────────────────────────
Write-Host "[4/6] Merging resources..." -ForegroundColor Yellow

$srcRes = "$V2raySrc\app\src\main\res"
$dstRes = "$WlrusDst\app\src\main\res"
if (Test-Path $srcRes) {
    Copy-Item -Recurse "$srcRes\*" "$dstRes\" -Force
    Write-Host "  OK - Resources merged (WLRUS theme overrides applied)" -ForegroundColor Green
}

$srcAssets = "$V2raySrc\app\src\main\assets"
$dstAssets = "$WlrusDst\app\src\main\assets"
if (Test-Path $srcAssets) {
    New-Item -ItemType Directory -Path $dstAssets -Force | Out-Null
    Copy-Item -Recurse "$srcAssets\*" "$dstAssets\" -Force
    Write-Host "  OK - Assets copied" -ForegroundColor Green
}

# ── 5. Native library (libv2ray.aar) ─────────────────────────────────────────
Write-Host "[5/6] Getting libv2ray.aar (~52 MB)..." -ForegroundColor Yellow

$dstLibs = "$WlrusDst\app\libs"
New-Item -ItemType Directory -Path $dstLibs -Force | Out-Null

$existing = Get-ChildItem $dstLibs -Filter "libv2ray.aar" -ErrorAction SilentlyContinue
if ($existing) {
    Write-Host "  OK - libv2ray.aar already present, skipping download" -ForegroundColor Green
}
elseif ($DownloadLibs) {
    $success = Get-LibvrayAar -dstLibs $dstLibs
    if (-not $success) {
        Write-Host "  Automatic download failed." -ForegroundColor Red
        Show-ManualLibsInstructions -dstLibs $dstLibs
    }
} else {
    Write-Host "  Skipped" -ForegroundColor Gray
    Show-ManualLibsInstructions -dstLibs $dstLibs
}

# ── 6. local.properties ───────────────────────────────────────────────────────
Write-Host "[6/6] Creating local.properties..." -ForegroundColor Yellow

$localProps = "$WlrusDst\local.properties"
if (-not (Test-Path $localProps)) {
    $sdkPath = "$env:LOCALAPPDATA\Android\Sdk"
    "sdk.dir=$($sdkPath.Replace('\','\\'))" | Set-Content $localProps -Encoding UTF8
    if (Test-Path $sdkPath) {
        Write-Host "  OK - sdk.dir=$sdkPath" -ForegroundColor Green
    } else {
        Write-Host "  Created. Edit local.properties and set the correct sdk.dir path." -ForegroundColor Yellow
    }
} else {
    Write-Host "  Skipped - already exists" -ForegroundColor Gray
}

# ── Done ──────────────────────────────────────────────────────────────────────
$hasAar = Test-Path "$WlrusDst\app\libs\libv2ray.aar"

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
if ($hasAar) {
    Write-Host "  All done! Ready to build." -ForegroundColor Green
} else {
    Write-Host "  Done - but libv2ray.aar is missing!" -ForegroundColor Yellow
    Write-Host "  See instructions above to get it manually." -ForegroundColor Yellow
}
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Next steps:" -ForegroundColor White
Write-Host "  1. Open WLRUS\ in Android Studio" -ForegroundColor Gray
Write-Host "  2. Edit BuiltinSubscriptions.kt  (add your subscription URLs)" -ForegroundColor Gray
Write-Host "  3. .\gradlew assemblePlayDebug" -ForegroundColor Gray
Write-Host ""
