<?php
header("Content-Type: application/json");
require_once "connection.php";

// Validate POST data
if (
    empty($_POST['title']) ||
    empty($_POST['date_time']) ||
    empty($_POST['uid']) ||
    !isset($_FILES['file'])
) {
    echo json_encode([
        "status" => false,
        "message" => "Missing required fields"
    ]);
    exit;
}

$title     = $_POST['title'];
$notes     = $_POST['notes'] ?? '';
$uid     = $_POST['uid'] ?? '';
$date_time = $_POST['date_time'];

$file      = $_FILES['file'];

$allowedTypes = [
    "image/jpeg",
    "image/png",
    "application/pdf"
];

// Validate file type
if (!in_array($file['type'], $allowedTypes)) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid file type"
    ]);
    exit;
}

// Create upload directory if not exists
$uploadDir = "uploads/medical/";
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

// Generate safe file name
$extension = pathinfo($file['name'], PATHINFO_EXTENSION);
$newFileName = time() . "_" . uniqid() . "." . $extension;
$targetPath = $uploadDir . $newFileName;

// Move uploaded file
if (!move_uploaded_file($file['tmp_name'], $targetPath)) {
    echo json_encode([
        "status" => false,
        "message" => "File upload failed"
    ]);
    exit;
}

// Insert into database
$stmt = $con->prepare(
    "INSERT INTO medical_history (uid, title, file_name, date_time, notes, status)
     VALUES (?, ?, ?, ?, ?, 'active')"
);

$stmt->bind_param(
    "sssss",
    $uid,
    $title,
    $targetPath,
    $date_time,
    $notes
);

if ($stmt->execute()) {
    echo json_encode([
        "status" => true,
        "message" => "Medical history added",
        "file" => $targetPath
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Database insert failed"
    ]);
}

$stmt->close();
$con->close();