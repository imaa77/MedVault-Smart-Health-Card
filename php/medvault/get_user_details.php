<?php

header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/connection.php';

function jsonResponse($statusCode, $data) {
    http_response_code($statusCode);
    echo json_encode($data);
    exit;
}

$uid = isset($_POST['uid']) ? trim($_POST['uid']) : null;


$sql = "SELECT * FROM user_details WHERE id = '$uid' ";
// echo $sql; exit; 

$result = mysqli_query($con, $sql);
if ($result === false) {
    jsonResponse(200, ['success' => false, 'message' => 'Database query failed.']);
}

while ($row = mysqli_fetch_assoc($result)) {
    $user[] = $row;    
}
mysqli_free_result($result);


jsonResponse(200, [
    'success' => true,
    'message' => 'Fetch successful.',
    'user' => $user
]);

?>
