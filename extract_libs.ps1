# Extract libhev-socks5-tunnel.so and libv2ray.so from v2rayNG APK
# Usage: .\extract_libs.ps1 path\to\v2rayNG.apk
#
# Download APK from: https://github.com/2dust/v2rayNG/releases/latest

param([Parameter(Mandatory)][string]$ApkPath)

$ProjectRoot = $PSScriptRoot
$Abis  = @("arm64-v8a", "armeabi-v7a", "x86_64")
$Needed = @("libhev-socks5-tunnel.so", "libv2ray.so")

Add-Type -AssemblyName System.IO.Compression.FileSystem

$apk = [System.IO.Compression.ZipFile]::OpenRead((Resolve-Path $ApkPath))

foreach ($entry in $apk.Entries) {
    $parts = $entry.FullName -split "/"
    if ($parts.Length -ne 3 -or $parts[0] -ne "lib") { continue }
    $abi  = $parts[1]
    $file = $parts[2]
    if ($Abis -notcontains $abi -or $Needed -notcontains $file) { continue }

    $destDir  = Join-Path $ProjectRoot "app\src\main\jniLibs\$abi"
    $destFile = Join-Path $destDir $file
    New-Item -ItemType Directory -Force $destDir | Out-Null

    $stream = $entry.Open()
    $out    = [System.IO.File]::Create($destFile)
    $stream.CopyTo($out)
    $out.Close(); $stream.Close()

    $kb = [math]::Round((Get-Item $destFile).Length / 1024)
    Write-Host "  OK  $abi/$file  ($kb KB)"
}

# Check for libv2ray.aar inside the APK
foreach ($entry in $apk.Entries) {
    if ($entry.FullName -like "*libv2ray.aar") {
        $dest = Join-Path $ProjectRoot "app\libs\libv2ray.aar"
        New-Item -ItemType Directory -Force (Split-Path $dest) | Out-Null
        $stream = $entry.Open()
        $out    = [System.IO.File]::Create($dest)
        $stream.CopyTo($out)
        $out.Close(); $stream.Close()
        $kb = [math]::Round((Get-Item $dest).Length / 1024)
        Write-Host "  OK  libs/libv2ray.aar  ($kb KB)"
    }
}

$apk.Dispose()
Write-Host ""
Write-Host "Done. Run: .\gradlew.bat assemblePlayDebug"
