<?php
error_reporting(0);
ini_set('display_errors', 0);

$servername = "127.0.0.1";
$username   = "root";
$password   = "";
$database   = "medvault";
$port       = 3307;

$con = new mysqli($servername, $username, $password, $database, $port);

if ($con->connect_error) {
    header("Content-Type: application/json");
    echo json_encode([
        "success" => false,
        "message" => "Database connection failed"
    ]);
    exit;
}
?>