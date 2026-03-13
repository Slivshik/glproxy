<?php
/**
 * WLRUS Secure Proxy Server
 * 
 * This PHP script acts as a secure proxy for encrypted subscription requests.
 * Flow: Client -> Your Server (decrypt & forward) -> Target Subscription Server
 * 
 * Usage:
 *   POST /proxy.php
 *   Body: {"crypt_url": "wlrus://crypt/...", "method": "GET", "headers": {...}}
 */

// Load secure crypt library
require_once __DIR__ . '/wlrus_crypt_secure.php';

// Enable error logging but don't display errors to clients
ini_set('display_errors', 0);
ini_set('log_errors', 1);
error_reporting(E_ALL);

header('Content-Type: application/json');

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['success' => false, 'error' => 'Method not allowed. Use POST.']);
    exit;
}

// Get JSON input
$input = json_decode(file_get_contents('php://input'), true);

if (json_last_error() !== JSON_ERROR_NONE) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => 'Invalid JSON']);
    exit;
}

// Validate required fields
if (!isset($input['crypt_url'])) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => 'crypt_url is required']);
    exit;
}

$cryptUrl = $input['crypt_url'];
$method = isset($input['method']) ? strtoupper($input['method']) : 'GET';
$customHeaders = isset($input['headers']) && is_array($input['headers']) ? $input['headers'] : [];
$postData = isset($input['post_data']) ? $input['post_data'] : null;

// Verify it's a valid crypt URL
if (!WlrusCrypt::isCryptUrl($cryptUrl)) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => 'Invalid crypt URL format. Must start with wlrus://crypt/']);
    exit;
}

// Decrypt the URL
$decryptedUrl = WlrusCrypt::decrypt($cryptUrl);

if ($decryptedUrl === false) {
    http_response_code(403);
    echo json_encode(['success' => false, 'error' => 'Failed to decrypt URL. Invalid key or corrupted data.']);
    exit;
}

// Validate decrypted URL is HTTP/HTTPS
if (!preg_match('#^https?://#i', $decryptedUrl)) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => 'Decrypted URL must be HTTP or HTTPS']);
    exit;
}

error_log("Proxy request: $method $decryptedUrl");

// Prepare headers for target server
$requestHeaders = [];

// Add default headers if not provided
if (!isset($customHeaders['User-Agent'])) {
    $requestHeaders[] = 'User-Agent: WLRUS-Proxy/1.0';
}

// Add any custom headers from client
foreach ($customHeaders as $headerName => $headerValue) {
    // Skip hop-by-hop headers that shouldn't be forwarded
    $lowerName = strtolower($headerName);
    if (in_array($lowerName, ['host', 'connection', 'keep-alive', 'transfer-encoding'])) {
        continue;
    }
    $requestHeaders[] = "$headerName: $headerValue";
}

// Initialize cURL
$ch = curl_init();

curl_setopt_array($ch, [
    CURLOPT_URL => $decryptedUrl,
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_MAXREDIRS => 5,
    CURLOPT_TIMEOUT => 30,
    CURLOPT_CONNECTTIMEOUT => 10,
    CURLOPT_SSL_VERIFYPEER => true,
    CURLOPT_SSL_VERIFYHOST => 2,
    CURLOPT_HTTPHEADER => $requestHeaders,
    CURLOPT_CUSTOMREQUEST => $method,
]);

// Add POST data if present
if ($postData !== null && in_array($method, ['POST', 'PUT', 'PATCH'])) {
    if (is_array($postData)) {
        curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($postData));
        $requestHeaders[] = 'Content-Type: application/x-www-form-urlencoded';
    } else {
        curl_setopt($ch, CURLOPT_POSTFIELDS, $postData);
    }
    curl_setopt($ch, CURLOPT_HTTPHEADER, $requestHeaders);
}

// Execute request
$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curlError = curl_error($ch);
$curlErrno = curl_errno($ch);

curl_close($ch);

// Handle cURL errors
if ($curlErrno !== 0) {
    error_log("cURL error: $curlError");
    http_response_code(502);
    echo json_encode([
        'success' => false,
        'error' => 'Failed to fetch target URL',
        'details' => $curlError
    ]);
    exit;
}

// Return successful response
echo json_encode([
    'success' => true,
    'http_code' => $httpCode,
    'data' => $response,
    'original_url' => $decryptedUrl,
    'message' => 'Request proxied successfully'
]);

?>
