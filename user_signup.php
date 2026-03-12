<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Content-Type: application/json; charset=UTF-8");

require_once "connection.php";

if ($_SERVER['REQUEST_METHOD'] != "POST") {
    echo json_encode([
        "success" => false,
        "message" => "Method not allowed. Use POST"
    ]);
    exit;
}

/* ==============================
   REQUIRED FIELDS CHECK
================================ */

$required = ["name","dob","blood","address","email","phone","password"];

foreach($required as $field){
    if(!isset($_POST[$field]) || trim($_POST[$field]) == ""){
        echo json_encode([
            "success" => false,
            "message" => "Missing field: $field"
        ]);
        exit;
    }
}

/* ==============================
   COLLECT DATA
================================ */

$name      = trim($_POST['name']);
$dob       = trim($_POST['dob']);   // YYYY-MM-DD format
$blood     = trim($_POST['blood']);
$allergies = trim($_POST['allergies'] ?? "");
$address   = trim($_POST['address']);
$email     = trim($_POST['email']);
$phone     = trim($_POST['phone']);
$password  = trim($_POST['password']);

$emergency_name  = trim($_POST['emergency_name'] ?? "");
$emergency_email = trim($_POST['emergency_email'] ?? "");
$emergency_phone = trim($_POST['emergency_phone'] ?? "");

/* ==============================
   CHECK IF USER EXISTS
================================ */

$check = $con->prepare("SELECT id FROM user_details WHERE phone = ? OR email = ?");
$check->bind_param("ss", $phone, $email);
$check->execute();
$check->store_result();

if ($check->num_rows > 0) {
    echo json_encode([
        "success" => false,
        "message" => "User already exists"
    ]);
    exit;
}
$check->close();

/* ==============================
   INSERT USER
================================ */

$stmt = $con->prepare("
    INSERT INTO user_details
    (name, dob, blood_group, allergies, address, email, phone, password, status, emergency_name, emergency_email, emergency_phone)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'active', ?, ?, ?)
");

$stmt->bind_param(
    "sssssssssss",
    $name,
    $dob,
    $blood,
    $allergies,
    $address,
    $email,
    $phone,
    $password,
    $emergency_name,
    $emergency_email,
    $emergency_phone
);

if ($stmt->execute()) {

    $new_id = $stmt->insert_id;
    $public_id = "MV" . $new_id;

    $update = $con->prepare("UPDATE user_details SET public_id=? WHERE id=?");
    $update->bind_param("si", $public_id, $new_id);
    $update->execute();
    $update->close();

    echo json_encode([
        "success" => true,
        "message" => "Signup successful",
        "public_id" => $public_id
    ]);

} else {

    echo json_encode([
        "success" => false,
        "message" => $stmt->error
    ]);
}

$stmt->close();
$con->close();
?>