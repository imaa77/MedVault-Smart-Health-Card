<?php
header("Content-Type: application/json");
include("connection.php");

if ($_SERVER['REQUEST_METHOD'] != "POST") {
    echo json_encode([
        "success" => false,
        "message" => "Method not allowed. Use POST"
    ]);
    exit;
}

$public_id = trim($_POST['public_id'] ?? "");
$otp       = trim($_POST['otp'] ?? "");

if ($public_id == "" || $otp == "") {
    echo json_encode([
        "success" => false,
        "message" => "Missing fields"
    ]);
    exit;
}

/*
IMPORTANT FIX:
We compare expiry using MySQL NOW() because we inserted expiry using MySQL NOW()
so timezone will match perfectly.
*/

$stmt = $con->prepare("
    SELECT id, otp, expires_at
    FROM doctor_access
    WHERE public_id = ?
      AND otp = ?
      AND expires_at >= NOW()
    ORDER BY id DESC
    LIMIT 1
");

$stmt->bind_param("ss", $public_id, $otp);
$stmt->execute();
$res = $stmt->get_result();

if ($res->num_rows == 0) {
    echo json_encode([
        "success" => false,
        "message" => "Invalid OTP or expired"
    ]);
    exit;
}

echo json_encode([
    "success" => true,
    "message" => "Doctor access granted"
]);

$stmt->close();
$con->close();
?>
