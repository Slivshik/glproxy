# ============================================================
#  WLRUS - Gradle Wrapper Bootstrap
#  Run this ONCE before running gradlew.bat
#
#  This downloads gradle-wrapper.jar (~60 KB) which is required
#  by gradlew.bat but cannot be included as a text file.
#
#  Usage (from WLRUS\ folder):
#    Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
#    .\setup_gradle_wrapper.ps1
# ============================================================

$wrapperDir = "$PSScriptRoot\gradle\wrapper"
$jarPath    = "$wrapperDir\gradle-wrapper.jar"

Write-Host "WLRUS Gradle Wrapper Setup" -ForegroundColor Cyan
Write-Host ""

if (Test-Path $jarPath) {
    $size = (Get-Item $jarPath).Length
    if ($size -gt 10000) {
        Write-Host "  gradle-wrapper.jar already present ($([math]::Round($size/1KB))KB) - nothing to do." -ForegroundColor Green
        Write-Host ""
        Write-Host "  You can now run:  .\gradlew.bat assemblePlayDebug" -ForegroundColor White
        exit 0
    }
}

New-Item -ItemType Directory -Path $wrapperDir -Force | Out-Null

# gradle-wrapper.jar is a fixed, versioned artifact - same file for all projects
# using the same Gradle version. Official sources:
$urls = @(
    # From Gradle's own GitHub (tagged release)
    "https://raw.githubusercontent.com/gradle/gradle/v9.3.1/gradle/wrapper/gradle-wrapper.jar",
    # From a known Android project mirror
    "https://raw.githubusercontent.com/android/nowinandroid/main/gradle/wrapper/gradle-wrapper.jar",
    # Generic latest
    "https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar"
)

$downloaded = $false

foreach ($url in $urls) {
    Write-Host "  Downloading from: $url" -ForegroundColor Gray
    try {
        Invoke-WebRequest -Uri $url -OutFile $jarPath -UseBasicParsing -TimeoutSec 30
        $size = (Get-Item $jarPath).Length
        if ($size -gt 10000) {
            Write-Host "  OK - gradle-wrapper.jar ($([math]::Round($size/1KB)) KB)" -ForegroundColor Green
            $downloaded = $true
            break
        }
    } catch {
        Write-Host "  Failed: $($_.Exception.Message)" -ForegroundColor DarkGray
    }

    # Try curl as fallback
    try {
        & curl.exe -sL -o $jarPath $url --max-time 30
        $size = (Get-Item $jarPath -ErrorAction SilentlyContinue).Length
        if ($size -gt 10000) {
            Write-Host "  OK via curl - gradle-wrapper.jar ($([math]::Round($size/1KB)) KB)" -ForegroundColor Green
            $downloaded = $true
            break
        }
    } catch {}
}

if (-not $downloaded) {
    Write-Host ""
    Write-Host "  Could not download gradle-wrapper.jar automatically." -ForegroundColor Red
    Write-Host ""
    Write-Host "  ALTERNATIVE: Use Android Studio instead of gradlew" -ForegroundColor Yellow
    Write-Host "  1. Open Android Studio" -ForegroundColor Gray
    Write-Host "  2. File -> Open -> select the WLRUS\ folder" -ForegroundColor Gray
    Write-Host "  3. Android Studio downloads Gradle automatically" -ForegroundColor Gray
    Write-Host "  4. Click Build -> Make Project" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  OR: Install Gradle globally" -ForegroundColor Yellow
    Write-Host "  1. Download Gradle 9.3.1 from https://gradle.org/releases/" -ForegroundColor Gray
    Write-Host "  2. Extract to C:\Gradle\gradle-9.3.1\" -ForegroundColor Gray
    Write-Host "  3. Add C:\Gradle\gradle-9.3.1\bin to your PATH" -ForegroundColor Gray
    Write-Host "  4. Then run:  gradle assemblePlayDebug" -ForegroundColor Gray
    Write-Host "     (note: 'gradle' not '.\gradlew')" -ForegroundColor Gray
    exit 1
}

Write-Host ""
Write-Host "  Setup complete!" -ForegroundColor Green
Write-Host ""
Write-Host "  Now run:" -ForegroundColor White
Write-Host "    .\gradlew.bat assemblePlayDebug" -ForegroundColor Cyan
Write-Host ""
Write-Host "  First build downloads Gradle 9.3.1 (~150 MB) - this takes a few minutes." -ForegroundColor Gray
Write-Host "  Subsequent builds are fast." -ForegroundColor Gray
Write-Host ""
