#!/usr/bin/env python3
"""
Extract libhev-socks5-tunnel.so and libv2ray.so from v2rayNG APK.

Usage:
  1. Download latest v2rayNG APK from:
     https://github.com/2dust/v2rayNG/releases/latest
     (get the universal APK, e.g. v2rayNG_x.x.x.apk)

  2. Run: python3 extract_libs.py path/to/v2rayNG.apk

  This will extract all needed .so files and libv2ray.aar into the correct
  locations inside your project.
"""

import sys
import os
import zipfile
import shutil

NEEDED_SO = [
    "libhev-socks5-tunnel.so",
    "libv2ray.so",
]

ABIS = ["arm64-v8a", "armeabi-v7a", "x86_64"]

def extract(apk_path: str, project_root: str):
    if not os.path.exists(apk_path):
        print(f"ERROR: APK not found: {apk_path}")
        sys.exit(1)

    jni_base = os.path.join(project_root, "app", "src", "main", "jniLibs")
    libs_dir  = os.path.join(project_root, "app", "libs")
    os.makedirs(libs_dir, exist_ok=True)

    found = {abi: [] for abi in ABIS}

    with zipfile.ZipFile(apk_path, "r") as apk:
        for entry in apk.namelist():
            # .so files live at lib/<abi>/libXxx.so inside the APK
            parts = entry.split("/")
            if len(parts) != 3 or parts[0] != "lib":
                continue
            abi, fname = parts[1], parts[2]
            if abi not in ABIS or fname not in NEEDED_SO:
                continue

            dest_dir  = os.path.join(jni_base, abi)
            dest_path = os.path.join(dest_dir, fname)
            os.makedirs(dest_dir, exist_ok=True)

            with apk.open(entry) as src, open(dest_path, "wb") as dst:
                shutil.copyfileobj(src, dst)

            size_kb = os.path.getsize(dest_path) // 1024
            print(f"  ✓ {abi}/{fname}  ({size_kb} KB)")
            found[abi].append(fname)

        # Also extract classes.dex / libv2ray.aar if present
        for entry in apk.namelist():
            if entry.endswith("libv2ray.aar"):
                dest = os.path.join(libs_dir, "libv2ray.aar")
                with apk.open(entry) as src, open(dest, "wb") as dst:
                    shutil.copyfileobj(src, dst)
                size_kb = os.path.getsize(dest) // 1024
                print(f"  ✓ libs/libv2ray.aar  ({size_kb} KB)")

    print()
    for abi in ABIS:
        for lib in NEEDED_SO:
            status = "✓" if lib in found[abi] else "✗ MISSING"
            print(f"  {status}  {abi}/{lib}")

    # libv2ray.aar check
    aar = os.path.join(libs_dir, "libv2ray.aar")
    print(f"  {'✓' if os.path.exists(aar) else '✗ MISSING'}  libs/libv2ray.aar")
    print()
    print("Done. Now run:  .\\gradlew.bat assemblePlayDebug")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print(__doc__)
        sys.exit(1)

    apk  = sys.argv[1]
    root = sys.argv[2] if len(sys.argv) > 2 else os.path.dirname(os.path.abspath(__file__))
    print(f"Extracting from: {apk}")
    print(f"Project root:    {root}")
    print()
    extract(apk, root)
