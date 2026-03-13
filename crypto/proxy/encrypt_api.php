<?php
/**
 * WLRUS Secure Encryption API
 * 
 * REST API endpoint for encrypting subscription URLs with enhanced security.
 * Uses environment variables or secure config file for key storage.
 * 
 * Usage:
 *   POST /api/encrypt.php
 *   Content-Type: application/json
 *   Body: {"url": "https://example.com/sub", "key_index": 0}
 */

// Load secure crypt library
require_once __DIR__ . '/wlrus_crypt_secure.php';

header('Content-Type: application/json');

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['success' => false, 'error' => 'Method not allowed']);
    exit;
}

// Get JSON input
$input = json_decode(file_get_contents('php://input'), true);

if (json_last_error() !== JSON_ERROR_NONE) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => 'Invalid JSON']);
    exit;
}

if (!isset($input['url'])) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => 'URL is required']);
    exit;
}

$url = $input['url'];
$keyIndex = isset($input['key_index']) ? intval($input['key_index']) : 0;

// Validate URL format
if (!filter_var($url, FILTER_VALIDATE_URL)) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => 'Invalid URL format']);
    exit;
}

// Validate key index
if ($keyIndex < 0 || $keyIndex > 4) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => 'Key index must be between 0 and 4']);
    exit;
}

// Encrypt the URL
$encrypted = WlrusCrypt::encryptSubscriptionUrl($url, $keyIndex);

if ($encrypted === false) {
    http_response_code(500);
    echo json_encode(['success' => false, 'error' => 'Encryption failed']);
    exit;
}

// Get public key info for response
$publicKeys = WlrusCrypt::getPublicKeys();

// Return success response
echo json_encode([
    'success' => true,
    'encrypted_url' => $encrypted,
    'key_index_used' => $keyIndex,
    'key_fingerprint' => $publicKeys[$keyIndex],
    'message' => 'URL encrypted successfully. Share this wlrus://crypt/ URL with users.'
]);

?>
