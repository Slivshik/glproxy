<?php
/**
 * WLRUS Crypt REST API Example
 * 
 * This is an example REST API endpoint for encrypting subscription URLs
 * Usage: POST /api/encrypt_subscription.php
 * Body: {"url": "https://example.com/sub", "key_index": 0}
 */

require_once 'wlrus_crypt.php';

header('Content-Type: application/json');

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['error' => 'Method not allowed']);
    exit;
}

// Get JSON input
$input = json_decode(file_get_contents('php://input'), true);

if (!isset($input['url'])) {
    http_response_code(400);
    echo json_encode(['error' => 'URL is required']);
    exit;
}

$url = $input['url'];
$keyIndex = isset($input['key_index']) ? intval($input['key_index']) : 0;

// Validate key index
if ($keyIndex < 0 || $keyIndex > 4) {
    http_response_code(400);
    echo json_encode(['error' => 'Key index must be between 0 and 4']);
    exit;
}

// Encrypt the URL
$encrypted = WlrusCrypt::encryptSubscriptionUrl($url, $keyIndex);

if ($encrypted === false) {
    http_response_code(500);
    echo json_encode(['error' => 'Encryption failed']);
    exit;
}

// Return success response
echo json_encode([
    'success' => true,
    'encrypted_url' => $encrypted,
    'key_index_used' => $keyIndex,
    'message' => 'URL encrypted successfully. Share this wlrus://crypt/ URL with users.'
]);

?>
