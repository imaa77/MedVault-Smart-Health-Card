<?php

header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/connection.php';

function jsonResponse($statusCode, $data) {
    http_response_code($statusCode);
    echo json_encode($data);
    exit;
}

/* ==============================
   GET USER ID
================================ */

$uid = isset($_POST['uid']) ? trim($_POST['uid']) : "";

if ($uid == "") {
    jsonResponse(200, [
        "success" => false,
        "message" => "User ID missing"
    ]);
}

/* ==============================
   FETCH REMINDERS
================================ */

$stmt = $con->prepare("
    SELECT id,title,description,time,start_date,days,created_at,status
    FROM reminders
    WHERE uid = ?
");

$stmt->bind_param("s", $uid);
$stmt->execute();

$result = $stmt->get_result();

$reminders = [];

$now = new DateTime(); // current datetime

while ($row = $result->fetch_assoc()) {

    $startDate = $row['start_date']; // yyyy-mm-dd
    $time      = $row['time'];       // HH:mm
    $days      = (int)$row['days'];

    if ($days <= 0) {
        continue;
    }

    // Create start datetime
    $startDateTime = DateTime::createFromFormat(
        'Y-m-d H:i',
        $startDate . ' ' . $time
    );

    if (!$startDateTime) {
        continue;
    }

    // Calculate end datetime
    $endDateTime = clone $startDateTime;
    $endDateTime->modify('+' . ($days - 1) . ' days');

    // Check reminder still active
    if ($now <= $endDateTime) {

        $row['end_datetime'] = $endDateTime->format('Y-m-d H:i');

        $reminders[] = [
            "id" => $row['id'],
            "title" => $row['title'],
            "description" => $row['description'],
            "time" => $row['time'],
            "start_date" => $row['start_date'],
            "days" => $row['days'],
            "created_at" => $row['created_at'],
            "status" => $row['status'],
            "end_datetime" => $row['end_datetime']
        ];
    }
}

$stmt->close();

/* ==============================
   RESPONSE
================================ */

if (count($reminders) == 0) {

    jsonResponse(200, [
        "success" => false,
        "message" => "No reminders found"
    ]);
}

jsonResponse(200, [
    "success" => true,
    "message" => "Reminders loaded",
    "reminders" => $reminders
]);

?>