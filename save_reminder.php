<?php
header("Content-Type: application/json");
require_once "connection.php";

if (
    empty($_POST['uid']) ||
    empty($_POST['title']) ||
    empty($_POST['descp']) ||
    empty($_POST['time']) ||
    empty($_POST['start_date']) ||
    empty($_POST['days'])
) {
    echo json_encode([
        "status" => false,
        "message" => "Missing fields"
    ]);
    exit;
}

$uid = $_POST['uid'];
$title = $_POST['title'];
$descp = $_POST['descp'];
$time = $_POST['time'];
$start_date = $_POST['start_date'];
$days = $_POST['days'];

$stmt = $con->prepare(
    "INSERT INTO reminders 
     (uid, title, description, time, start_date, days)
     VALUES (?, ?, ?, ?, ?, ?)"
);

$stmt->bind_param(
    "sssssi",
    $uid,
    $title,
    $descp,
    $time,
    $start_date,
    $days
);

if ($stmt->execute()) {
    echo json_encode([
        "status" => true,
        "message" => "Reminder saved"
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Database error"
    ]);
}

$stmt->close();
$con->close();