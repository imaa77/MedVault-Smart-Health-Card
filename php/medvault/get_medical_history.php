<?php

header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/connection.php';

function jsonResponse($statusCode, $data) {
    http_response_code($statusCode);
    echo json_encode($data);
    exit;
}


$uid = isset($_POST['uid']) ? trim($_POST['uid']) : null;

$sql = "SELECT * FROM medical_history WHERE uid = '$uid' ";
$result = mysqli_query($con, $sql);
if ($result === false) {
    jsonResponse(200, ['success' => false, 'message' => 'Database query failed.']);
}

while($row = mysqli_fetch_assoc($result)) {
    $user[] = $row;
}
mysqli_free_result($result);

if (!$user) {
    jsonResponse(200, ['success' => false, 'message' => 'Invalid email or password.']);
}

// $user[] = $row;

// jsonResponse(401, ['success' => false, 'message' => 'Invalid email or password.']);

jsonResponse(200, [
    'success' => true,
    'message' => 'Login successful.',
    'history' => $user
]);

?>
