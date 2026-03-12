<?php
header("Content-Type: application/json");
require_once "connection.php";

if (
    empty($_POST['uid']) ||
    empty($_POST['medicine']) ||
    empty($_POST['dosage']) ||
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
$medicine = $_POST['medicine'];
$dosage = $_POST['dosage'];
$time = $_POST['time'];
$start_date = $_POST['start_date'];
$days = $_POST['days'];

$stmt = $con->prepare(
    "INSERT INTO medicine_reminders 
     (uid, medicine, dosage, time, start_date, days)
     VALUES (?, ?, ?, ?, ?, ?)"
);

$stmt->bind_param(
    "sssssi",
    $uid,
    $medicine,
    $dosage,
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