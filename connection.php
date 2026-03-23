<?php
error_reporting(0);
ini_set('display_errors', 0);

$servername = "sql207.infinityfree.com";
$username   = "if0_41454102";
$password   = "Medvault20003";
$database   = "if0_41454102_medvault";

$con = new mysqli($servername, $username, $password, $database);

if ($con->connect_error) {
    header("Content-Type: application/json");
    echo json_encode([
        "success" => false,
        "message" => "Database connection failed: " . $con->connect_error
    ]);
    exit;
}
?>