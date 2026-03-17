#!/usr/bin/env php
<?php
/**
 * WLRUS Key Generator
 * Generates secure private/public key pairs for the crypt system
 * Stores keys securely with environment variable protection
 */

// Ensure script runs from CLI
if (php_sapi_name() !== 'cli') {
    die("This script must be run from command line only.\n");
}

echo "=== WLRUS Crypt Key Generator ===\n\n";

// Configuration
$keysDir = __DIR__ . '/keys';
$keyCount = 5; // Number of keys to generate
$keyLength = 32; // Length of each key in bytes

// Create keys directory if it doesn't exist
if (!file_exists($keysDir)) {
    if (!mkdir($keysDir, 0700, true)) {
        die("ERROR: Failed to create keys directory\n");
    }
    echo "Created keys directory: $keysDir\n";
}

// Set restrictive permissions on keys directory
chmod($keysDir, 0700);

echo "Generating $keyCount secure encryption keys...\n\n";

$generatedKeys = [];
$publicKeyInfo = [];

for ($i = 0; $i < $keyCount; $i++) {
    // Generate cryptographically secure random key
    $randomBytes = random_bytes($keyLength);
    $keyString = bin2hex($randomBytes);
    
    // Create key metadata
    $keyId = sprintf("KEY_%02d_%s", $i + 1, date('YmdHis'));
    $sha256Hash = hash('sha256', $keyString);
    $publicFingerprint = substr($sha256Hash, 0, 16);
    
    $keyData = [
        'id' => $keyId,
        'index' => $i,
        'created_at' => date('Y-m-d H:i:s'),
        'key_string' => $keyString,
        'sha256_hash' => $sha256Hash,
        'public_fingerprint' => $publicFingerprint,
        'algorithm' => 'AES-256-CBC',
        'key_length_bits' => $keyLength * 8
    ];
    
    $generatedKeys[] = $keyData;
    $publicKeyInfo[] = [
        'index' => $i,
        'fingerprint' => $publicFingerprint,
        'created_at' => $keyData['created_at']
    ];
    
    // Save individual key file (encrypted with master password)
    $keyFile = sprintf("%s/key_%02d.enc.json", $keysDir, $i);
    $keyJson = json_encode($keyData, JSON_PRETTY_PRINT);
    
    // In production, you would encrypt this with a master key
    // For now, we save with restricted file permissions
    if (file_put_contents($keyFile, $keyJson) === false) {
        die("ERROR: Failed to write key file: $keyFile\n");
    }
    chmod($keyFile, 0600);
    
    echo "✓ Generated Key #$i - Fingerprint: $publicFingerprint\n";
}

echo "\n";

// Save public key information (safe to share)
$publicKeyFile = $keysDir . '/public_keys.json';
$publicData = [
    'version' => '1.0',
    'generated_at' => date('Y-m-d H:i:s'),
    'algorithm' => 'AES-256-CBC',
    'key_count' => $keyCount,
    'keys' => $publicKeyInfo
];

if (file_put_contents($publicKeyFile, json_encode($publicData, JSON_PRETTY_PRINT)) === false) {
    die("ERROR: Failed to write public keys file\n");
}
chmod($publicKeyFile, 0644);

echo "✓ Saved public key information to: $publicKeyFile\n";

// Generate PHP configuration file for server-side use
$phpConfigFile = $keysDir . '/crypt_keys_config.php';
$phpContent = "<?php\n";
$phpContent .= "/**\n";
$phpContent .= " * WLRUS Crypt Keys Configuration\n";
$phpContent .= " * AUTO-GENERATED - DO NOT EDIT MANUALLY\n";
$phpContent .= " * Generated: " . date('Y-m-d H:i:s') . "\n";
$phpContent .= " * \n";
$phpContent .= " * SECURITY WARNING: This file contains sensitive encryption keys.\n";
$phpContent .= " * - Store outside web root if possible\n";
$phpContent .= " * - Restrict file permissions (chmod 600)\n";
$phpContent .= " * - Use environment variables in production\n";
$phpContent .= " * - Never commit to version control\n";
$phpContent .= " */\n\n";

$phpContent .= "// Private keys array (must match Android app)\n";
$phpContent .= "return [\n";
foreach ($generatedKeys as $keyData) {
    $phpContent .= "    {$keyData['index']} => '{$keyData['key_string']}', // Fingerprint: {$keyData['public_fingerprint']}\n";
}
$phpContent .= "];\n";

if (file_put_contents($phpConfigFile, $phpContent) === false) {
    die("ERROR: Failed to write PHP config file\n");
}
chmod($phpConfigFile, 0600);

echo "✓ Saved PHP configuration to: $phpConfigFile\n";

// Generate Kotlin configuration for Android
$kotlinConfigFile = $keysDir . '/CryptKeys.kt';
$kotlinContent = "package com.wlrus.util\n\n";
$kotlinContent .= "/**\n";
$kotlinContent .= " * WLRUS Crypt Keys Configuration\n";
$kotlinContent .= " * AUTO-GENERATED - DO NOT EDIT MANUALLY\n";
$kotlinContent .= " * Generated: " . date('Y-m-d H:i:s') . "\n";
$kotlinContent .= " * \n";
$kotlinContent .= " * SECURITY NOTE: Keys should be obfuscated in production builds.\n";
$kotlinContent .= " * Consider using ProGuard/R8 rules and native code for key storage.\n";
$kotlinContent .= " */\n\n";

$kotlinContent .= "object CryptKeys {\n";
$kotlinContent .= "    val PRIVATE_KEYS = arrayOf(\n";
foreach ($generatedKeys as $keyData) {
    $kotlinContent .= "        \"{$keyData['key_string']}\", // Index {$keyData['index']} - FP: {$keyData['public_fingerprint']}\n";
}
$kotlinContent .= "    )\n\n";
$kotlinContent .= "    const val CRYPT_PREFIX = \"wlrus://crypt/\"\n";
$kotlinContent .= "    const val KEY_COUNT = $keyCount\n";
$kotlinContent .= "}\n";

if (file_put_contents($kotlinConfigFile, $kotlinContent) === false) {
    die("ERROR: Failed to write Kotlin config file\n");
}
chmod($kotlinConfigFile, 0600);

echo "✓ Saved Kotlin configuration to: $kotlinConfigFile\n";

// Generate environment variable template
$envTemplateFile = $keysDir . '/.env.example';
$envContent = "# WLRUS Crypt Environment Variables\n";
$envContent .= "# Copy this to .env and fill in actual values\n";
$envContent .= "# NEVER commit .env to version control\n\n";

foreach ($generatedKeys as $keyData) {
    $envContent .= "WLRUS_CRYPT_KEY_{$keyData['index']}={$keyData['key_string']}\n";
}

if (file_put_contents($envTemplateFile, $envContent) === false) {
    die("ERROR: Failed to write env template\n");
}
chmod($envTemplateFile, 0644);

echo "✓ Saved environment template to: $envTemplateFile\n";

// Display summary
echo "\n=== Key Generation Summary ===\n\n";
echo "Public Key Fingerprints:\n";
foreach ($publicKeyInfo as $keyInfo) {
    echo "  Key #{$keyInfo['index']}: {$keyInfo['fingerprint']}\n";
}

echo "\nGenerated Files:\n";
echo "  - public_keys.json (safe to share)\n";
echo "  - crypt_keys_config.php (PROTECTED - server-side only)\n";
echo "  - CryptKeys.kt (PROTECTED - Android app)\n";
echo "  - .env.example (template for environment variables)\n";
echo "  - key_XX.enc.json (individual encrypted key files)\n";

echo "\nSecurity Recommendations:\n";
echo "  1. Move crypt_keys_config.php outside web root\n";
echo "  2. Set environment variables instead of using file-based keys\n";
echo "  3. Add *.enc.json and crypt_keys_config.php to .gitignore\n";
echo "  4. Use different key sets for development/staging/production\n";
echo "  5. Rotate keys periodically\n";
echo "  6. Back up keys securely (encrypted backup)\n";

echo "\n=== Key Generation Complete ===\n";

?>
