<?php

header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/connection.php';


function jsonResponse($statusCode, $data) {
    http_response_code($statusCode);
    echo json_encode($data);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(200, ['success' => false, 'message' => 'Method not allowed. Use POST.']);
}

$inputEmail = null;
$inputPassword = null;

if (!empty($_POST)) {
    $inputEmail = isset($_POST['email']) ? trim($_POST['email']) : null;
    $inputPassword = isset($_POST['password']) ? $_POST['password'] : null;
} else {
    $raw = file_get_contents('php://input');
    if ($raw) {
        $json = json_decode($raw, true);
        if (is_array($json)) {
            $inputEmail = isset($json['email']) ? trim($json['email']) : null;
            $inputPassword = isset($json['password']) ? $json['password'] : null;
        }
    }
}

if (empty($inputEmail) || empty($inputPassword)) {
    jsonResponse(200, ['success' => false, 'message' => 'Email and password are required.']);
}

if (!filter_var($inputEmail, FILTER_VALIDATE_EMAIL)) {
    jsonResponse(200, ['success' => false, 'message' => 'Invalid email format.']);
}

if (!isset($con) || !($con instanceof mysqli)) {
    jsonResponse(200, ['success' => false, 'message' => 'Database connection not available.']);
}

// escape email to avoid injection (note: prepared statements are safer)
$email_esc = mysqli_real_escape_string($con, $inputEmail);
$sql = "SELECT * FROM user_details WHERE email = '{$email_esc}' AND password = '$inputPassword' LIMIT 1";
$result = mysqli_query($con, $sql);
if ($result === false) {
    jsonResponse(200, ['success' => false, 'message' => 'Database query failed.']);
}

$row = mysqli_fetch_assoc($result);
mysqli_free_result($result);

if (!$row) {
    jsonResponse(200, ['success' => false, 'message' => 'Invalid email or password.']);
}

$user = $row;

// jsonResponse(401, ['success' => false, 'message' => 'Invalid email or password.']);

jsonResponse(200, [
    'success' => true,
    'message' => 'Login successful.',
    'user' => $user
]);

?>
